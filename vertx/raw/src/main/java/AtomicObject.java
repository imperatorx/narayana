/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



import java.io.IOException;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.ObjectModel;
import com.arjuna.ats.arjuna.ObjectType;
import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.txoj.Lock;
import com.arjuna.ats.txoj.LockManager;
import com.arjuna.ats.txoj.LockMode;
import com.arjuna.ats.txoj.LockResult;

public class AtomicObject extends LockManager
{
    public AtomicObject()
    {
        this(ObjectModel.SINGLE);
    }

    public AtomicObject(int om)
    {
        super(ObjectType.ANDPERSISTENT, om);

        state = 0;

        AtomicAction A = new AtomicAction();

        A.begin();

        if (setlock(new Lock(LockMode.WRITE), 0) == LockResult.GRANTED)
        {
            if (A.commit() == ActionStatus.COMMITTED)
                System.out.println("Created persistent object " + get_uid());
            else
                System.out.println("Action.commit error.");
        }
        else
        {
            A.abort();

            System.out.println("setlock error.");
        }

        String debug = System.getProperty("DEBUG", null);

        if (debug != null)
            printDebug = true;
    }

    public AtomicObject(Uid u)
    {
        this(u, ObjectModel.SINGLE);
    }

    public AtomicObject(Uid u, int om)
    {
        super(u, ObjectType.ANDPERSISTENT, om);

        state = -1;

        AtomicAction A = new AtomicAction();

        A.begin();

        if (setlock(new Lock(LockMode.READ), 0) == LockResult.GRANTED)
        {
            System.out.println("Recreated object " + u);
            A.commit();
        }
        else
        {
            System.out.println("Error recreating object " + u);
            A.abort();
        }

        String debug = System.getProperty("DEBUG", null);

        if (debug != null)
            printDebug = true;
    }

    public int getRetry ()
    {
        return retry;
    }
    
    public void setRetry (int t)
    {
        retry = t;
    }
    
    public void terminate ()
    {
        super.terminate();
    }
    
    public void incr (int value) throws TestException
    {
        AtomicAction A = new AtomicAction();

        A.begin();

        if (setlock(new Lock(LockMode.WRITE), retry) == LockResult.GRANTED)
        {
            state += value;

            if (A.commit() != ActionStatus.COMMITTED)
                throw new TestException("Action commit error.");
            else
                return;
        }
        else
        {
            if (printDebug)
                System.out.println("Error - could not set write lock.");
        }

        A.abort();

        throw new TestException("Write lock error.");
    }

    public void set (int value) throws TestException
    {
        AtomicAction A = new AtomicAction();

        A.begin();

        if (setlock(new Lock(LockMode.WRITE), retry) == LockResult.GRANTED)
        {
            state = value;

            if (A.commit() != ActionStatus.COMMITTED)
                throw new TestException("Action commit error.");
            else
                return;
        }
        else
        {
            if (printDebug)
                System.out.println("Error - could not set write lock.");
        }

        A.abort();

        throw new TestException("Write lock error.");
    }

    public int get () throws TestException
    {
        AtomicAction A = new AtomicAction();
        int value = -1;

        A.begin();

        if (setlock(new Lock(LockMode.READ), retry) == LockResult.GRANTED)
        {
            value = state;

            if (A.commit() == ActionStatus.COMMITTED)
                return value;
            else
                throw new TestException("Action commit error.");
        }
        else
        {
            if (printDebug)
                System.out.println("Error - could not set read lock.");
        }

        A.abort();

        throw new TestException("Read lock error.");
    }

    public boolean save_state (OutputObjectState os, int ot)
    {
        boolean result = super.save_state(os, ot);

        if (!result)
            return false;

        try
        {
            os.packInt(state);
        }
        catch (IOException e)
        {
            result = false;
        }

        return result;
    }

    public boolean restore_state (InputObjectState os, int ot)
    {
        boolean result = super.restore_state(os, ot);

        if (!result)
            return false;

        try
        {
            state = os.unpackInt();
        }
        catch (IOException e)
        {
            result = false;
        }

        return result;
    }

    public String type ()
    {
        return "/StateManager/LockManager/AtomicObject";
    }

    private int state;

    private boolean printDebug;
    private int retry = 0;
}