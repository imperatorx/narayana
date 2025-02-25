/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.services.framework.task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.arjuna.webservices.logging.WSCLogger;

/**
 * This class manages the client side of the task manager
 * 
 * @author kevin
 */
public class TaskManager
{
    /**
     * The singleton.
     */
    private static final TaskManager MANAGER = new TaskManager() ;

    /**
     * The default maximum worker count.
     */
    private static final int DEFAULT_MAXIMUM_THREAD_COUNT = 10 ;

    /**
     * The default minimum worker count.
     */
    private static final int DEFAULT_MINIMUM_THREAD_COUNT = 0 ;

    /**
     * The minimum worker pool count.
     */
    private int minimumWorkerCount = DEFAULT_MINIMUM_THREAD_COUNT ;

    /**
     * The maximum worker pool count.
     */
    private int maximumWorkerCount = DEFAULT_MAXIMUM_THREAD_COUNT ;

    /**
     * The set of already allocated workers.
     */
    private Set workerPool = new HashSet() ;

    /**
     * The current list of tasks.
     */
    private LinkedList taskList = new LinkedList() ;

    /**
     * The counter used for naming the threads.
     */
    private int taskCount ;

    /**
     * The number of worker waiting.
     */
    private int waitingCount ;

    /**
     * A flag indicating that shutdown is in progress.
     */
    private boolean shutdown ;

    /**
     * Get the singleton controlling the tasks.
     * 
     * @return The task manager.
     */
    public static TaskManager getManager()
    {
        return MANAGER ;
    }

    /**
     * Private to prevent initialisation.
     */
    private TaskManager()
    {
    }

    /**
     * Queue the task for execution.
     * 
     * @param task The task to be executed.
     * @return true if the task was queued, false otherwise.
     * 
     */
    public boolean queueTask(final Task task)
    {
        final boolean debugEnabled = WSCLogger.logger.isTraceEnabled() ;
        synchronized(workerPool)
        {
            if (shutdown)
            {
                if (debugEnabled)
                {
                    WSCLogger.logger.tracef("Shutdown in progress, ignoring task '%s'", task) ;
                }
                return false ;
            }
        }

        final boolean notify ;
        synchronized(taskList)
        {
            taskList.addLast(task) ;
            notify = (waitingCount > 0) ;
            if (notify)
            {
                if (debugEnabled)
                {
                    WSCLogger.logger.tracef("queueTask: notifying waiting workers (%s) for task '%s'",
                            Integer.valueOf(waitingCount), task) ;
                }
                taskList.notify() ;
            }
        }

        final boolean create ;
        synchronized(workerPool)
        {
            create = ((workerPool.size() < minimumWorkerCount) ||
                    ((workerPool.size() < maximumWorkerCount) && !notify)) ;
        }

        if (create)
        {
            if (debugEnabled)
            {
                WSCLogger.logger.tracef("queueTask: creating worker for task '%s'", task) ;
            }
            createWorker() ;
        }
        else if (debugEnabled)
        {
            WSCLogger.logger.tracef("queueTask: queueing task '%s' for execution", task) ;
        }
        
        return true ;
    }

    /**
     * Set the minimum worker count for the pool.
     * 
     * @param minimumWorkerCount The minimum worker count.
     *
     */
    public void setMinimumWorkerCount(final int minimumWorkerCount)
    {
        final boolean debugEnabled = WSCLogger.logger.isTraceEnabled() ;
        synchronized(workerPool)
        {
            if (shutdown)
            {
                if (debugEnabled)
                {
                    WSCLogger.logger.tracev("shutdown in progress, ignoring set minimum worker count") ;
                }
                return ;
            }
            this.minimumWorkerCount = (minimumWorkerCount < 0 ? DEFAULT_MINIMUM_THREAD_COUNT
                    : minimumWorkerCount) ;
            if (this.minimumWorkerCount > maximumWorkerCount)
            {
                maximumWorkerCount = this.minimumWorkerCount ;
            }

            if (debugEnabled)
            {
                WSCLogger.logger.tracev("setMinimumWorkerCount: {0}") ;
            }
        }

        while(true)
        {
            final boolean create ;
            synchronized(workerPool)
            {
                create = (workerPool.size() < this.minimumWorkerCount) ;
            }

            if (create)
            {
                createWorker() ;
            }
            else
            {
                break ;
            }
        }
    }

    /**
     * Get the minimum worker count for the pool.
     * 
     * @return The minimum worker count.
     */
    public int getMinimumWorkerCount()
    {
        synchronized(workerPool)
        {
            return minimumWorkerCount ;
        }
    }

    /**
     * Set the maximum worker count for the pool.
     * 
     * @param maximumWorkerCount The maximum worker count.
     */
    public void setMaximumWorkerCount(final int maximumWorkerCount)
    {
        final boolean debugEnabled = WSCLogger.logger.isTraceEnabled() ;
        synchronized(workerPool)
        {
            if (shutdown)
            {
                if (debugEnabled)
                {
                    WSCLogger.logger.tracev("shutdown in progress, ignoring set maximum worker count") ;
                }
                return ;
            }
            this.maximumWorkerCount = (maximumWorkerCount < 0 ? DEFAULT_MAXIMUM_THREAD_COUNT
                    : maximumWorkerCount) ;
            if (minimumWorkerCount > this.maximumWorkerCount)
            {
                minimumWorkerCount = this.maximumWorkerCount ;
            }

            if (debugEnabled)
            {
                WSCLogger.logger.tracev("setMaximumWorkerCount: {0}",
                        new Object[] {this.maximumWorkerCount}) ;
            }

            synchronized(taskList)
            {
                if ((workerPool.size() > this.maximumWorkerCount)
                        && (waitingCount > 0))
                {
                    if (debugEnabled)
                    {
                        WSCLogger.logger.tracev("setMaximumWorkerCount: reducing pool size from {0} to {1}",
                                new Object[] {workerPool.size(), this.maximumWorkerCount}) ;
                    }
                    taskList.notify() ;
                }
            }
        }
    }

    /**
     * Get the maximum worker count for the pool.
     * 
     * @return The maximum worker count.
     */
    public int getMaximumWorkerCount()
    {
        synchronized(workerPool)
        {
            return maximumWorkerCount ;
        }
    }

    /**
     * Get the current worker count for the pool.
     * 
     * @return The current worker count.
     */
    public int getWorkerCount()
    {
        synchronized(workerPool)
        {
            return workerPool.size() ;
        }
    }

    /**
     * Close all threads and reset the task list. This method waits until all
     * threads have finished before returning.
     */
    public void shutdown()
    {
        final boolean debugEnabled = WSCLogger.logger.isTraceEnabled() ;

        synchronized(workerPool)
        {
            if (shutdown)
            {
                if (debugEnabled)
                {
                    WSCLogger.logger.tracev("Shutdown already in progress") ;
                }
            }
            else
            {
                setMaximumWorkerCount(0) ;
                shutdown = true ;
            }
        }

        while(true)
        {
            final Thread waitThread ;
            synchronized(workerPool)
            {
                final Iterator workerPoolIter = workerPool.iterator() ;
                if (workerPoolIter.hasNext())
                {
                    waitThread = (Thread) workerPoolIter.next() ;
                }
                else
                {
                    waitThread = null ;
                }
            }

            if (waitThread == null)
            {
                break ;
            }
            else
            {
                try
                {
                    waitThread.join() ;
                }
                catch (final InterruptedException ie)
                {
                } // Ignore
            }
        }

        synchronized(workerPool)
        {
            if (shutdown)
            {
                taskList.clear() ;
                shutdown = false ;
            }
        }
    }

    /**
     * Get another task from the pool.
     * 
     * @return The next task from the pool or null if finished.
     * 
     */
    Task getTask()
    {
        final boolean debugEnabled = WSCLogger.logger.isTraceEnabled() ;

        while(true)
        {
            final boolean remove ;
            synchronized(workerPool)
            {
                final int excessCount = workerPool.size() - maximumWorkerCount ;
                if (excessCount > 0)
                {
                    if (debugEnabled)
                    {
                        WSCLogger.logger.tracev("getTask: releasing thread") ;
                    }
                    synchronized(taskList)
                    {
                        if ((excessCount > 1) && (waitingCount > 0))
                        {
                            if (debugEnabled)
                            {
                                WSCLogger.logger.tracev("getTask: notifying waiting thread about excess count {0}",
                                        new Object[] {excessCount}) ;
                            }
                            taskList.notify() ;
                        }
                    }
                    remove = true ;
                }
                else
                {
                    remove = false ;
                }
            }

            if (remove)
            {
                final Thread currentThread = Thread.currentThread() ;
                synchronized(workerPool)
                {
                    workerPool.remove(currentThread) ;
                }
                return null ;
            }

            synchronized(taskList)
            {
                final int numTasks = taskList.size() ;
                if (numTasks > 0)
                {
                    final Task task = (Task) taskList.removeFirst() ;
                    if ((numTasks > 1) && (waitingCount > 0))
                    {
                        taskList.notify() ;
                    }
                    if (debugEnabled)
                    {
                        WSCLogger.logger.tracev("getTask: returning task {0}", task) ;
                    }
                    return task ;
                }
                waitingCount++ ;
                if (debugEnabled)
                {
                    WSCLogger.logger.tracev("getTask: waiting for task for {0} number of times", waitingCount) ;
                }
                try
                {
                    taskList.wait() ;
                }
                catch (final InterruptedException ie)
                {
                    if (debugEnabled)
                    {
                        WSCLogger.logger.tracev("getTask: interrupted") ;
                    }
                }
                finally
                {
                    waitingCount-- ;
                }
            }
        }
    }

    /**
     * Create and register a task worker.
     */
    private void createWorker()
    {
        final TaskWorker taskWorker = new TaskWorker(this) ;
        final String name ;
        synchronized(workerPool)
        {
            name = "TaskWorker-" + ++taskCount ;
        }
        final Thread thread = new Thread(taskWorker, name) ;
        thread.setDaemon(true) ;
        synchronized(workerPool)
        {
            workerPool.add(thread) ;
        }
        thread.start() ;
    }
}