/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.arjuna.coordinator;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.arjuna.coordinator.listener.ReaperMonitor;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.internal.arjuna.coordinator.ReaperElement;
import com.arjuna.ats.internal.arjuna.coordinator.ReaperElementManager;
import com.arjuna.ats.internal.arjuna.coordinator.ReaperThread;
import com.arjuna.ats.internal.arjuna.coordinator.ReaperWorkerThread;

/**
 * Class to record transactions with non-zero timeout values, and class to
 * implement a transaction reaper thread which terminates these transactions
 * once their timeout elapses.
 *
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: TransactionReaper.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.0.
 */

public class TransactionReaper
{

    public static final String NORMAL = "NORMAL";

    public static final String DYNAMIC = "DYNAMIC";

    public static final String PERIODIC = "PERIODIC"; // the new name for 'NORMAL'

    private TransactionReaper(long checkPeriod)
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("TransactionReaper::TransactionReaper ( " + checkPeriod
                    + " )");
        }

        _checkPeriod = checkPeriod;
    }

    public final long checkingPeriod()
    {
        if (_dynamic) {
            return nextDynamicCheckTime.get() - System.currentTimeMillis();
        } else {
            // if we have a cancel in progress which needs
            // checking up on then we have to wake up in time
            // for it whether we are using a static or
            // dynamic model

            final ReaperElement head = _reaperElements.getFirst();
            if(head != null) {
                if (head._status != ReaperElement.RUN) {
                    long waitTime = head.getNextCheckAbsoluteMillis() - System.currentTimeMillis();
                    if (waitTime < _checkPeriod) {
                        return waitTime;
                    }
                }
            }

            return _checkPeriod;
        }
    }

    /**
     * process all entries in the timeout queue which have
     * expired. entries for newly expired transactions are passed
     * to a worker thread for cancellation and requeued for
     * subsequent progress checks. the worker is given a kick if
     * such checks find it is wedged.
     * 
     * Timeout is given in milliseconds.
     *
     * Runs on the ReaperThread
     */
    public final void check()
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("TransactionReaper::check ()");
        }

        do {
            final ReaperElement reaperElement;

            synchronized(this) {
                final long now = System.currentTimeMillis();
                final long next = nextDynamicCheckTime.get();

                if (tsLogger.logger.isTraceEnabled()) {
                    tsLogger.logger.trace("TransactionReaper::check comparing now="+now+" to next="+next);
                }

                if (now < next) {
                    break;
                }

                reaperElement = _reaperElements.getFirst();
                // TODO close window where first can change - maybe record nextDynamicCheckTime before probing first,
                // then use compareAndSet? Although something will need to check before sleeping anyhow...
                if (reaperElement == null) {
                    nextDynamicCheckTime.set(Long.MAX_VALUE);
                    return;
                } else {
                    final long nextCheck = reaperElement.getNextCheckAbsoluteMillis();
                    if(nextCheck > now) {
                        nextDynamicCheckTime.set(nextCheck);
                        return; // nothing to do yet.
                    }
                }
            }

            if(reaperElement._status != ReaperElement.RUN ||
                    reaperElement.getNextCheckAbsoluteMillis() >= reaperElement.getTransactionTimeoutAbsoluteMillis()){
                // log warning, except when we're just going to perform a stacktrace.
                tsLogger.i18NLogger.warn_coordinator_TransactionReaper_18(reaperElement._control.get_uid(), reaperElement.statusName());
            }

            // if we have to synchronize on multiple objects we always
            // do so in a fixed order ReaperElement before Reaper and
            // ReaperElement before Reaper._cancelQueue in order to
            // ensure we don't deadlock. We never sychronize on the
            // reaper and the cancel queue at the same time.

            synchronized(reaperElement) {
                switch (reaperElement._status) {
                    case ReaperElement.TRACE:
                        // either the worker is running slow (perhaps it got wedged on another tx's cancel) or
                        // the getTransactionTimeoutAbsoluteMillis was very close to the getNextCheckAbsoluteMillis.
                        // Either way, the worker didn't complete the stackTrace and return the state to RUN yet
                        // But no matter, we can treat this case as RUN, which will either reschedule to the next
                        // TRACE time, or move us to SCHEDULE_CANCEL
                    // no break; here, we want to fall through...
                    case ReaperElement.RUN: {

                        if(reaperElement.getNextCheckAbsoluteMillis() < reaperElement.getTransactionTimeoutAbsoluteMillis()) {
                            // we haven't reached the timeout yet, we just want to perform a stacktrace capture, not a cancellation

                            if (tsLogger.logger.isTraceEnabled()) {
                                tsLogger.logger.trace("Reaper scheduling TX for stackTrace " + reaperElement._control.get_uid());
                            }

                            reaperElement._status = ReaperElement.TRACE;

                            long now = System.currentTimeMillis();
                            long remaining = reaperElement.getTransactionTimeoutAbsoluteMillis()-now;
                            if(remaining > _traceInterval) {
                                reinsertElement(reaperElement, _traceInterval);
                            } else {
                                reinsertElement(reaperElement, remaining);
                            }

                        } else {
                            // this tx has just timed out. remove it from the
                            // TX list, update the timeout to take account of
                            // cancellation period and reinsert as a cancelled
                            // TX. this ensures we process it again if it does
                            // not get cancelled in time

                            if (tsLogger.logger.isTraceEnabled()) {
                                tsLogger.logger.trace("Reaper scheduling TX for cancellation " + reaperElement._control.get_uid());
                            }

                            reaperElement._status = ReaperElement.SCHEDULE_CANCEL;
                            reinsertElement(reaperElement, _cancelWaitPeriod);
                        }

                        // insert into work queue for a worker
                        // thread to process and then make sure a worker
                        // thread is awake

                        synchronized(_workQueue) {
                            _workQueue.add(reaperElement);
                            _workQueue.notifyAll();
                        }
                    }
                    break;
                    case ReaperElement.SCHEDULE_CANCEL: {
                        // hmm, a worker is taking its time to
                        // start processing this scheduled entry.
                        // we may just be running slow ... but the
                        // worker may be wedged under a cancel for
                        // some other TX. add an extra delay to
                        // give the worker more time to complete
                        // its current task and progress this
                        // entry to the CANCEL state. if the
                        // worker *is* wedged then this will
                        // ensure the wedged TX entry comes to the
                        // front of the queue.

                        reinsertElement(reaperElement, _cancelWaitPeriod);

                        if (tsLogger.logger.isTraceEnabled()) {
                            tsLogger.logger.trace("Reaper deferring interrupt for TX scheduled for cancel " + reaperElement._control.get_uid());
                        }
                    }
                    break;
                    case ReaperElement.CANCEL: {
                        // ok, the worker must be wedged under a
                        // call to cancel() -- kick the thread and
                        // reschedule the element for a later
                        // check to ensure the thread responded to
                        // the kick
                    	
                    	StringBuilder sb = new StringBuilder();
                        for (StackTraceElement element : reaperElement._worker.getStackTrace()) {
                            sb.append(element.toString());
                            sb.append("\n");
                        }                    	
                        tsLogger.i18NLogger.wedged_reaperelement(sb.toString());

                        reaperElement._status = ReaperElement.CANCEL_INTERRUPTED;

                        reaperElement._worker.interrupt();

                        reinsertElement(reaperElement, _cancelFailWaitPeriod);

                        // log that we interrupted cancel()

                        if (tsLogger.logger.isTraceEnabled()) {
                            tsLogger.logger.trace("TransactionReaper::check interrupting cancel in progress for " + reaperElement._control.get_uid());
                        }
                    }
                    break;
                    case ReaperElement.CANCEL_INTERRUPTED: {
                        // cancellation got truly wedged -- mark
                        // the element as a zombie so the worker
                        // exits when (if?) it wakes up and create
                        // a new worker thread to handle further
                        // cancellations. then mark the
                        // transaction as rollback only.

                        reaperElement._status = ReaperElement.ZOMBIE;

                        synchronized(this) {
                            _zombieCount++;

                            if (tsLogger.logger.isTraceEnabled()) {
                                tsLogger.logger.trace("Reaper " + Thread.currentThread() + " got a zombie " + reaperElement._worker + " (zombie count now " + _zombieCount + ") cancelling " + reaperElement._control.get_uid());
                            }

                            if (_zombieCount == _zombieMax) {
                                // log zombie overflow error call()

                                tsLogger.i18NLogger.error_coordinator_TransactionReaper_5(Integer.toString(_zombieCount));
                            }
                        }

                        _reaperWorkerThread = new ReaperWorkerThread(TransactionReaper._theReaper);
                        _reaperWorkerThread.setDaemon(true);

                        _reaperWorkerThread.start();

                        // log a failed cancel()

                        tsLogger.i18NLogger.warn_coordinator_TransactionReaper_6(reaperElement._worker.toString(),
                                reaperElement._control.get_uid());

                        // ok, since the worker was wedged we need to
                        // remove the entry from the timeouts and
                        // transactions lists then mark this tx as
                        // rollback only. we have to log a message
                        // whether we succeed, fail or get interrupted

                        removeElementReaper(reaperElement);

                        try {
                            if (reaperElement._control.preventCommit()) {

                                // log a successful preventCommit()

                                tsLogger.i18NLogger.warn_coordinator_TransactionReaper_10(reaperElement._control.get_uid());

                                notifyListeners(reaperElement._control, false);
                            } else {
                                // log a failed preventCommit()

                                tsLogger.i18NLogger.warn_coordinator_TransactionReaper_11(reaperElement._control.get_uid());
                            }
                        }
                        catch (Exception e1) {
                            // log an exception under preventCommit()

                            tsLogger.i18NLogger.warn_coordinator_TransactionReaper_12(reaperElement._control.get_uid(), e1);
                        }
                    }
                    break;
                    case ReaperElement.FAIL:
                    case ReaperElement.COMPLETE: {
                        // ok, the worker should remove the tx
                        // from the transactions queue very soon
                        // but we need to progress to the next
                        // entry so we will steal in and do it
                        // first

                        removeElementReaper(reaperElement);
                    }
                    break;

                }
            }
        } while (true);

    }

    /**
     * called by check, this method removes and reinserts an element in the timeout
     * ordered set, recalculating the next wakeup time accordingly.
     */
    private void reinsertElement(ReaperElement e, long delay)
    {
        synchronized (this) {
            long newWakeup = _reaperElements.reorder(e, delay);
            nextDynamicCheckTime.set(newWakeup); // TODO - set should be atomic with reorder?
        }
    }

    // runs on the ReaperWorkerThread
    public final void waitForWork()
    {
        synchronized (_workQueue) {
            try {
                while (_workQueue.isEmpty()) {
                    _workQueue.wait();
                }
            }
            catch (InterruptedException e) {
            }
        }
    }

    // runs on the ReaperWorkerThread
    public final void doWork()
    {
        for (; ;) {
            ReaperElement e;

            // see if we have any work to process

            synchronized (_workQueue) {
                try {
                    e = _workQueue.remove(0);
                }
                catch (IndexOutOfBoundsException ioobe) {
                    break;
                }
            }

            // perhaps we're just taking a thread snapshot, not cancelling the tx
            if(e._status == ReaperElement.TRACE) {

                if (tsLogger.logger.isTraceEnabled()) {
                    tsLogger.logger.trace("Reaper Worker " + Thread.currentThread() + " calling recordStackTraces for " + e._control.get_uid());
                }

                synchronized (e) {
                    e._control.recordStackTraces();
                    e._status = ReaperElement.RUN;
                }

                return; // the reaper re-queued the element already, so we don't have to.
            }

            // ok, if we get here current status must be SCHEDULE_CANCEL.
            // progress state to CANCEL and call cancel()

            if (tsLogger.logger.isTraceEnabled()) {
                tsLogger.logger.trace("Reaper Worker " + Thread.currentThread() + " attempting to cancel " + e._control.get_uid());
            }

            boolean cancelled = false;
            Exception exception = null;

            synchronized (e) {
                e._worker = Thread.currentThread();
                e._status = ReaperElement.CANCEL;
                e.notifyAll();
            }

            // we are now exposed to at most one interrupt from
            // the reaper. test for running and try the cancel if
            // required

            try {
                if (e._control.running()) {

                    e._control.outputCapturedStackTraces();

                    // try to cancel the transaction, note that if the
                	// transaction previously failed to abort due to a 
                	// runtimeexception being raised by the AbstractRecord
                	// implementation then the transaction will effectively
                	// remain untouched and afterCompletion will not be called.

                    if (e._control.cancel() == ActionStatus.ABORTED) {
                        cancelled = true;

                        if (TxStats.enabled()) {
                            // note that we also count timeouts as application rollbacks via
                            // the stats unpdate in the TwoPhaseCoordinator cancel() method.
                            TxStats.getInstance().incrementTimeouts();
                        }

                        notifyListeners(e._control, true);
                    }
                }
            }
            catch (Exception e1) {
                exception = e1;
            }

            // ok, close the interrupt window by resetting the
            // state -- unless we have been told to go away by
            // being set to ZOMBIE

            synchronized (e) {
                if (e._status == ReaperElement.ZOMBIE) {
                    // we need to decrement the zombie count and
                    // force an immediate thread exit. the reaper
                    // will have removed the entry from the
                    // transactions list and started another
                    // worker thread.

                    ReaperWorkerThread worker = (ReaperWorkerThread) Thread.currentThread();
                    worker.shutdown();

                    synchronized(this) {
                        _zombieCount--;
                    }

                    tsLogger.i18NLogger.warn_coordinator_TransactionReaper_13(Thread.currentThread().toString(),
                            e._control.get_uid(), Integer.toString(_zombieCount));

                    // this gets us out of the for(;;) loop and
                    // the shutdown call above makes sure we exit
                    // after returning

                    break;
                } else if (cancelled &&
                        e._status == ReaperElement.CANCEL_INTERRUPTED) {
                    // ok the call to cancel() returned true but
                    // we cannot trust it because the reaper sent
                    // the thread an interrupt

                    cancelled = false;
                    e._status = ReaperElement.FAIL;
                    e.notifyAll();
                } else {
                    e._status = (cancelled
                            ? ReaperElement.COMPLETE
                            : ReaperElement.FAIL);
                    e.notifyAll();
                }
            }

            // log a message notifying success, failure or
            // exception during cancel(), remove the element from
            // the transactions queue and mark TX as rollback only

            if (cancelled) {
                tsLogger.i18NLogger.warn_coordinator_TransactionReaper_7(Thread.currentThread().toString(),
                        e._control.get_uid());
            } else if (e._control.running()) {
                if (exception != null) {
                    tsLogger.i18NLogger.warn_coordinator_TransactionReaper_9(Thread.currentThread().toString(), e._control.get_uid(), exception);
                } else {
                    tsLogger.i18NLogger.warn_coordinator_TransactionReaper_8(Thread.currentThread().toString(),
                            e._control.get_uid());
                }

                try {
                    if (e._control.preventCommit()) {
                        // log a successful preventCommit()

                        tsLogger.i18NLogger.warn_coordinator_TransactionReaper_14(Thread.currentThread().toString(),
                                e._control.get_uid());

                        notifyListeners(e._control, false);
                    } else {
                        // log a failed preventCommit()

                        tsLogger.i18NLogger.warn_coordinator_TransactionReaper_15(Thread.currentThread().toString(),
                                e._control.get_uid());
                    }
                }
                catch (Exception e1) {
                    // log an exception under preventCommit()

                    tsLogger.i18NLogger.warn_coordinator_TransactionReaper_16(Thread.currentThread().toString(), e._control.get_uid(), e1);
                }
            }

            removeElementReaper(e);
        }
    }

    /**
     * @return the number of items in the reaper's list.
     * @since JTS 2.2.
     *
     * Note: this is a) expensive and b) an approximation. Should be called only by test code.
     */
    public final long numberOfTransactions()
    {
        return _reaperElements.size();
    }

    /**
     * Return the number of timeouts registered.
     * Note: this is a) expensive and b) an approximation. Should be called only by test code.
     *
     * @return The number of timeouts registered.
     */
    public final long numberOfTimeouts()
    {
        return _timeouts.size();
    }

    public final void addListener(ReaperMonitor listener)
    {
        _listeners.add(listener);
    }

    public final boolean removeListener(ReaperMonitor listener)
    {
        return _listeners.remove(listener);
    }

    /**
     * timeout is given in seconds, but we work in milliseconds.
     *
     * Attempting to insert an element that is already present is an error (IllegalStateException)
     */
    public final void insert(Reapable control, int timeout)
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("TransactionReaper::insert ( " + control + ", " + timeout
                    + " )");
        }

        /*
         * Ignore if the timeout is zero, since this means the transaction
         * should never timeout.
         */
        if (timeout == 0)
            return;

        ReaperElement reaperElement = new ReaperElement(control, timeout, _traceGracePeriod);

        _lifetime.addAndGet(timeout);

        // insert the element only if it's not already present. We check _timeouts first, as elements
        // maybe temporarily removed and reinserted in _reaperElements, so that is not as good a check.
        // We use lazy eval to ensure we insert to _reaperElements only if we inserted to _timeouts.
        // Note: removal works in reverse order i.e. _reaperElements then _timeouts.
        if ((_timeouts.putIfAbsent(reaperElement._control, reaperElement) == null)) {
            _reaperElements.add(reaperElement);
        } else {
            throw new IllegalStateException(tsLogger.i18NLogger.get_coordinator_TransactionReaper_1());
        }

        if (_dynamic && reaperElement.getNextCheckAbsoluteMillis() < nextDynamicCheckTime.get()) {
            updateCheckTimeForEarlierInsert(reaperElement.getNextCheckAbsoluteMillis());
        }
    }

    /**
     * Reset the next wakeup time, when a new element has a timeout earlier than the currently scheduled wakeup.
     *
     * @param newCheckTime absolute time in ms.
     */
    private void updateCheckTimeForEarlierInsert(long newCheckTime)
    {
        synchronized (this) {
            long oldCheckTime = nextDynamicCheckTime.get();
            while (newCheckTime < oldCheckTime) {
                if (nextDynamicCheckTime.compareAndSet(oldCheckTime, newCheckTime)) {
                    notifyAll(); // force recalc of next wakeup time, taking into account the newly inserted element(s)
                } else {
                    oldCheckTime = nextDynamicCheckTime.get();
                }
            }
        }
    }

    // takes an Object because OTSManager.destroyControl(Control|ControlImple) uses PseudoControlWrapper not Reapable
    public final void remove(Object control)
    {
        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("TransactionReaper::remove ( " + control + " )");
        }

        if (control == null)
            return;

        ReaperElement key = _timeouts.get(control);
        if (key == null) {
            return;
        }

        // if a cancellation is in progress then we have to
        // see it through as we have to ensure that the worker
        // thread does not get wedged. so we have to tell the
        // control has gone away. in order to test the status
        // we need to synchronize on the element before we
        // synchronize on this so we can ensure that we don't
        // deadlock ourselves.

        synchronized (key) {
            if (key._status != ReaperElement.RUN) {
                // we are cancelling this TX anyway and need
                // to track the progress of the cancellation
                // using this entry so we cnanot remove it
                return;
            }

            removeElementClient(key);
        }
    }

    /**
     * Given the transaction instance, this will return the time left before the
     * transaction is automatically rolled back if it has not been terminated.
     *
     * @param control
     * @return the remaining time in milliseconds.
     */
    public final long getRemainingTimeoutMills(Object control)
    {
        // arg is an Object because ArjunaTransactionImple.propagationContext does not have a Reapable

        if ((_timeouts.isEmpty()) || (control == null)) {
            if (tsLogger.logger.isTraceEnabled()) {
                tsLogger.logger.trace("TransactionReaper::getRemainingTimeout for " + control
                        + " returning 0");
            }

            return 0;
        }

        final ReaperElement reaperElement = _timeouts.get(control);
        long timeout = 0;

        if (reaperElement == null) {
            timeout = 0;
        } else {
            // units are in milliseconds at this stage.
            timeout = reaperElement.getTransactionTimeoutAbsoluteMillis() - System.currentTimeMillis();
        }

        if (tsLogger.logger.isTraceEnabled()) {
            tsLogger.logger.trace("TransactionReaper::getRemainingTimeoutMillis for "+control+" returning "+timeout);
        }

        return timeout;
    }

    /**
     * Given a Control, return the associated timeout, or 0 if we do not know
     * about it.
     * 
     * Return in seconds!
     *
     * Takes an Object because TransactionFactoryImple.getTransactionInfo and
     * ArjunaTransactionImple.propagationContext use it and don't have a Reapable.
     */
    public final int getTimeout(Object control)
    {
        if ((_timeouts.isEmpty()) || (control == null)) {
            if (tsLogger.logger.isTraceEnabled()) {
                tsLogger.logger.trace("TransactionReaper::getTimeout for " + control
                        + " returning 0");
            }

            return 0;
        }

        final ReaperElement reaperElement = _timeouts.get(control);

        int timeout = (reaperElement == null ? 0 : reaperElement._timeout);

        tsLogger.logger.trace("TransactionReaper::getTimeout for "+control+" returning "+timeout);

        return timeout;
    }

    /*
    * Terminate the transaction reaper. This is a synchronous operation
    * and will only return once the reaper has been shutdown cleanly.
    *
    * Note, this method assumes that the transaction system has been
    * shutdown already so no new transactions can be created, or we
    * could be here for a long time!
    *
    * @param waitForTransactions if <code>true</code> then the reaper will
    * wait until all transactions have terminated (or been terminated by it).
    * If <code>false</code> then the reaper will call setRollbackOnly on all
    * the transactions.
    */

    private final void shutdown(boolean waitForTransactions)
    {
        // the reaper thread synchronizes and waits on this

        synchronized (this) {
            _inShutdown = true;

            /*
                * If the caller does not want to wait for the normal transaction timeout
                * periods to elapse before terminating, then we first start by enabling
                * our time machine!
                */

            if (!waitForTransactions) {
                _reaperElements.setAllTimeoutsToZero();
            }

            /*
                * Wait for all of the transactions to
                * terminate normally.
                */
            while (!_reaperElements.isEmpty()) {
                try {
                    this.wait();
                }
                catch (final Exception ex) {
                }
            }

            _reaperThread.shutdown();

            notifyAll();
        }
        try {
            _reaperThread.join();
        }
        catch (final Exception ex) {
        }

        _reaperThread = null;

        // the reaper worker thread synchronizes and wais on the work queue

        synchronized (_workQueue) {
            _reaperWorkerThread.shutdown();
            _workQueue.notifyAll();
            // hmm, not sure we really need to do this but . . .
            _reaperWorkerThread.interrupt();
        }

        try {
            _reaperWorkerThread.join();
        }
        catch (final Exception ex) {
        }

        _reaperWorkerThread = null;
    }

    // called (indirectly) by user code doing removals on e.g. commit/rollback
    // does not reset the wakeup time - we prefer leaving an unnecessary wakeup as it's
    // cheaper than locking to recalculate the new time here.
    private final void removeElementClient(ReaperElement reaperElement)
    {
        _reaperElements.remove(reaperElement);        
        _timeouts.remove(reaperElement._control);

        // don't recalc time, just wake up as planned

        if(_inShutdown) {
            synchronized (this) {
                this.notifyAll(); // TODO: use different lock for shutdown?
            }
        }
    }

    /*
      * Remove element from list and trigger waiter if we are
      * being shutdown.
      *
      */
    // called internally by the reaper when removing elements - note the different
    // behaviour with regard to check time recalculation. Here we need to ensure the
    // new time is correct.
    private final void removeElementReaper(ReaperElement reaperElement)
    {
        _reaperElements.remove(reaperElement);
        _timeouts.remove(reaperElement._control);

        synchronized (this) {

            // TODO set needs tobe atomic to getFirst?
            ReaperElement first = _reaperElements.getFirst();
            if(first != null) {
                nextDynamicCheckTime.set(first.getNextCheckAbsoluteMillis());
            } else {
                nextDynamicCheckTime.set(Long.MAX_VALUE);
                if(_inShutdown) {
                    this.notifyAll(); // TODO: use different lock for shutdown?
                }
            }
        }
    }



    private final void notifyListeners(Reapable element, boolean rollback)
    {
        // notify listeners. Ignore errors.

        for (int i = 0; i < _listeners.size(); i++) {
            try {
                if (rollback)
                    _listeners.get(i).rolledBack(element.get_uid());
                else
                    _listeners.get(i).markedRollbackOnly(element.get_uid());
            }
            catch (final Throwable ex) {
                // ignore
            }
        }
    }

    /**
     * Currently we let the reaper thread run at same priority as other threads.
     * Could get priority from environment.
     */
    public static synchronized void instantiate()
    {
        if (TransactionReaper._theReaper == null)
        {
            if (tsLogger.logger.isTraceEnabled()) {
                tsLogger.logger.trace("TransactionReaper::instantiate()");
            }

            // default to dynamic mode
            TransactionReaper._dynamic = true;

            String mode = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperMode();

            if (mode.compareTo(TransactionReaper.PERIODIC) == 0) {
                TransactionReaper._dynamic = false;
            }

            if (mode.compareTo(TransactionReaper.NORMAL) == 0) {
                TransactionReaper._dynamic = false;

                tsLogger.i18NLogger.warn_coordinator_TransactionReaper_19();
            }

            long checkPeriod = Long.MAX_VALUE;
            if (!TransactionReaper._dynamic) {
                checkPeriod = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperTimeout();
            }
            TransactionReaper._theReaper = new TransactionReaper(checkPeriod);

            TransactionReaper._theReaper._cancelWaitPeriod = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperCancelWaitPeriod();

            // must give TX at least 10 millisecs to
            // respond to cancel

            if (TransactionReaper._theReaper._cancelWaitPeriod < 10) {
                TransactionReaper._theReaper._cancelWaitPeriod = 10;
            }

            TransactionReaper._theReaper._cancelFailWaitPeriod = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperCancelFailWaitPeriod();

            // must give TX at least 10 millisecs to
            // respond to cancel

            if (TransactionReaper._theReaper._cancelFailWaitPeriod < 10) {
                TransactionReaper._theReaper._cancelFailWaitPeriod = 10;
            }

            TransactionReaper._theReaper._zombieMax = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperZombieMax();

            // we start bleating if the zombie count
            // reaches zombieMax so it has to be at
            // least 1

            if (TransactionReaper._theReaper._zombieMax <= 0) {
                TransactionReaper._theReaper._zombieMax = 1;
            }

            TransactionReaper._theReaper._traceGracePeriod = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperTraceGracePeriod();
            TransactionReaper._theReaper._traceInterval = arjPropertyManager.getCoordinatorEnvironmentBean().getTxReaperTraceInterval();

            _reaperThread = new ReaperThread(TransactionReaper._theReaper);
            // _reaperThread.setPriority(Thread.MIN_PRIORITY);

            _reaperThread.setDaemon(true);

            _reaperWorkerThread = new ReaperWorkerThread(TransactionReaper._theReaper);
            _reaperWorkerThread.setDaemon(true);

            _reaperThread.start();

            _reaperWorkerThread.start();
        }
    }

    /**
     * Starting with 4.8, this method will always return an instance, will never return null.
     * This causes the reaper to be instantiated unnecessarily in some cases, but that's cheaper
     * than the alternatives.
     *
     * @return a TransactionReaper singleton.
     */
    public static TransactionReaper transactionReaper() {
        if(_theReaper == null) {
            instantiate();
        }
        return _theReaper;
    }

    /**
     * Terminate the transaction reaper. This is a synchronous operation
     * and will only return once the reaper has been shutdown cleanly.
     * 
     * Note, this method assumes that the transaction system has been
     * shutdown already so no new transactions can be created, or we
     * could be here for a long time!
     *
     * @param waitForTransactions if <code>true</code> then the reaper will
     *                            wait until all transactions have terminated (or been terminated by it).
     *                            If <code>false</code> then the reaper will call setRollbackOnly on all
     *                            the transactions.
     */

    public static synchronized void terminate(boolean waitForTransactions)
    {
        if (_theReaper != null) {
            _theReaper.shutdown(waitForTransactions);
            _theReaper = null;
        }
    }

    public static boolean isDynamic()
    {
        return _dynamic;
    }

    public static synchronized long transactionLifetime()
    {
        return _lifetime.get();
    }

    public static final long defaultCheckPeriod = 120000; // in milliseconds
    public static final long defaultCancelWaitPeriod = 500; // in milliseconds
    public static final long defaultCancelFailWaitPeriod = 500; // in milliseconds
    public static final long defaultUntracedPeriod = 180000; // in milliseconds
    public static final long defaultTracePeriod = 30000; // in milliseconds
    public static final int defaultZombieMax = 8;

    static final synchronized void reset()
    {
        _theReaper = null;
    }

    private final ReaperElementManager _reaperElements = new ReaperElementManager();

    // The keys are actually Reapable, as that's what insert takes. However, some functions use get(Object)
    // and rely on clever hashcode/equals behaviour, especially for the JTS. Thus the generics key type is Object.
    private final ConcurrentMap<Object, ReaperElement> _timeouts = new ConcurrentHashMap<Object, ReaperElement>();

    private final List<ReaperElement> _workQueue = new LinkedList<ReaperElement>();

    private final Vector<ReaperMonitor> _listeners = new Vector<ReaperMonitor>(); // TODO sync properly

    private long _checkPeriod = 0;

    // Although it is atomic, writes (but not reads) need to by synchronized(this) i.e. on the TransactionReaper instance
    // in order to ensure proper timing with respect to wait/notify and wakeups on the _reaperElements queue.
    private final AtomicLong nextDynamicCheckTime = new AtomicLong(Long.MAX_VALUE);

    /**
     * number of millisecs delay afer a cancel() is scheduled
     * before the reaper tries to interrupt the worker thread
     * executing the cancel()
     */
    private long _cancelWaitPeriod = 0;

    /**
     * number of millisecs delay afer a worker thread is
     * interrupted before the reaper writes the it off as a zombie
     * and starts a new thread
     */
    private long _cancelFailWaitPeriod = 0;

    /**
     * threshold for count of non-exited zombies at which system
     * starts logging error messages
     */
    private int _zombieMax = 0;

    private long _traceGracePeriod;
    private long _traceInterval;

    private static volatile TransactionReaper _theReaper = null;

    private static ReaperThread _reaperThread = null;

    private static ReaperWorkerThread _reaperWorkerThread = null;

    private static boolean _dynamic = true;

    private static AtomicLong _lifetime = new AtomicLong(0);

    private static int _zombieCount = 0;

	private boolean _inShutdown = false;
}