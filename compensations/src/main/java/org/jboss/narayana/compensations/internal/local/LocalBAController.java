/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */
package org.jboss.narayana.compensations.internal.local;

import com.arjuna.mw.wsas.activity.ActivityHierarchy;
import com.arjuna.mw.wsas.exceptions.SystemException;
import com.arjuna.mw.wscf.exceptions.ProtocolNotRegisteredException;
import com.arjuna.mw.wscf.model.sagas.exceptions.CoordinatorCancelledException;
import com.arjuna.mw.wscf11.model.sagas.CoordinatorManagerFactory;
import org.jboss.narayana.compensations.api.CompensationHandler;
import org.jboss.narayana.compensations.api.ConfirmationHandler;
import org.jboss.narayana.compensations.api.TransactionCompensatedException;
import org.jboss.narayana.compensations.api.TransactionLoggedHandler;
import org.jboss.narayana.compensations.internal.BAController;
import org.jboss.narayana.compensations.internal.BeanManagerUtil;
import org.jboss.narayana.compensations.internal.CompensationManagerImpl;
import org.jboss.narayana.compensations.internal.CompensationManagerState;
import org.jboss.narayana.compensations.internal.ParticipantManager;

import java.util.UUID;

/**
 * @author paul.robinson@redhat.com 19/04/2014
 */
public class LocalBAController implements BAController {

    @Override
    public void beginBusinessActivity() throws Exception {

        CoordinatorManagerFactory.coordinatorManager().begin("Sagas11HLS");
        CompensationManagerImpl.resume(new CompensationManagerState());
    }

    @Override
    public void closeBusinessActivity() throws Exception {

        CoordinatorManagerFactory.coordinatorManager().close();
        CompensationManagerImpl.suspend();
    }

    @Override
    public void cancelBusinessActivity() throws Exception {

        CoordinatorManagerFactory.coordinatorManager().cancel();
        CompensationManagerImpl.suspend();
    }

    @Override
    public void completeBusinessActivity(final boolean isException) throws Exception {

        if (CompensationManagerImpl.isCompensateOnly() && !isException) {
            cancelBusinessActivity();
            throw new TransactionCompensatedException("Transaction was marked as 'compensate only'");
        } else if (CompensationManagerImpl.isCompensateOnly()) {
            cancelBusinessActivity();
        } else {
            try {
                closeBusinessActivity();
            } catch (CoordinatorCancelledException e) {
                throw new TransactionCompensatedException("Failed to close transaction", e);
            }
        }
    }

    public boolean isBARunning() {

        try {
            return CoordinatorManagerFactory.coordinatorManager().currentActivity() != null;
        } catch (SystemException e) {
            return false;
        } catch (ProtocolNotRegisteredException e) {
            return false;
        }
    }

    public Object suspend() throws Exception {

        return CoordinatorManagerFactory.coordinatorManager().suspend();
    }

    public void resume(Object context) throws Exception {

        CoordinatorManagerFactory.coordinatorManager().resume((ActivityHierarchy) context);
    }


    @Override
    public Object getCurrentTransaction() throws Exception {

        return CoordinatorManagerFactory.coordinatorManager().currentActivity();
    }

    @Override
    public ParticipantManager enlist(Class<? extends CompensationHandler> compensationHandlerClass,
            Class<? extends ConfirmationHandler> confirmationHandlerClass,
            Class<? extends TransactionLoggedHandler> transactionLoggedHandlerClass) throws Exception {

        CompensationHandler compensationHandler = instantiate(compensationHandlerClass);
        ConfirmationHandler confirmationHandler = instantiate(confirmationHandlerClass);
        TransactionLoggedHandler transactionLoggedHandler = instantiate(transactionLoggedHandlerClass);

        return enlist(compensationHandler, confirmationHandler, transactionLoggedHandler);
    }

    @Override
    public ParticipantManager enlist(CompensationHandler compensationHandler, ConfirmationHandler confirmationHandler,
            TransactionLoggedHandler transactionLoggedHandler) throws Exception {

        String participantId = String.valueOf(UUID.randomUUID());
        LocalParticipant participant = new LocalParticipant(compensationHandler, confirmationHandler, transactionLoggedHandler,
                getCurrentTransaction(), participantId);

        CoordinatorManagerFactory.coordinatorManager().enlistParticipant(participant);

        return new LocalParticipantManager(participantId);
    }

    private <T> T instantiate(Class<T> clazz) {

        if (clazz == null) {
            return null;
        }

        return BeanManagerUtil.createBeanInstance(clazz);
    }

}
