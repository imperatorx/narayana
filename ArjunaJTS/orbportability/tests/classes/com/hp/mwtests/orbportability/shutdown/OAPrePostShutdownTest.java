/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.hp.mwtests.orbportability.shutdown;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.arjuna.orbportability.ORB;
import com.arjuna.orbportability.RootOA;

/**
 * @author Richard Begg
 */
public class OAPrePostShutdownTest implements PrePostTestCallback
{
    public static final int NONE = 0, PRESHUTDOWN = 1, POSTSHUTDOWN = 2, INVALID = 3;
    private static final String[] STATE_STRING = {"NONE", "PRESHUTDOWN", "POSTSHUTDOWN", "INVALID" };

    public int	_currentState;

    /**
     * Generates a String version of the 'enumerated value'.
     *
     * @param value The 'enumerated value' to generate the string of.
     * @return The String version of the enumerated value.
     */
    public static String PrettyPrintState(int value)
    {
    	if ( (value >= NONE) && (value <= INVALID) )
    	    return(STATE_STRING[value]);

    	return("##ERROR##");
    }

    /**
     * Called by the pre-shutdown subclass to inform test that it has been prodded
     *
     * @param name The name associated with this pre-shutdown callback
     */
    public void preShutdownCalled(String name)
    {
    	System.out.println( "Previous State : "+ PrettyPrintState( _currentState ) );

    	switch ( _currentState )
        {
    	    case NONE :
    	    case POSTSHUTDOWN :
    	    	_currentState = PRESHUTDOWN;
    	    	break;

    	    case PRESHUTDOWN :
    	    default :
    	    	_currentState = INVALID;
    	    	break;
    	}

    	System.out.println( " Current State : "+ PrettyPrintState( _currentState ) );
    }

    /**
     * Called by the post-shutdown subclass to inform test that it has been prodded
     *
     * @param name The name associated with this post-shutdown callback
     */
    public void postShutdownCalled(String name)
    {
    	System.out.println( "Previous State : "+ PrettyPrintState( _currentState ) );

    	switch ( _currentState )
        {
    	    case PRESHUTDOWN :
    	    	_currentState = POSTSHUTDOWN;
    	    	break;

    	    case NONE :
    	    case POSTSHUTDOWN :
    	    default :
    	    	_currentState = INVALID;
    	    	break;
    	}

    	System.out.println( " Current State : "+ PrettyPrintState( _currentState ) );
    }

    @Test
    public void test() throws Exception
    {
        ORB orb = ORB.getInstance("main_orb");
        RootOA oa = RootOA.getRootOA(orb);

        System.out.println("Initialising ORB and OA");

        orb.initORB(new String[] {}, null);
        oa.initOA();

        _currentState = NONE;

        /**
         * Register pre and post shutdown handlers
         */
        oa.addPreShutdown( new TestPreShutdown( "PreShutdown", this ) );
        oa.addPostShutdown( new TestPostShutdown( "PostShutdown", this ) );

        System.out.println("Shutting down ORB and OA");
        oa.destroy();
        orb.shutdown();

        /*
       * Ensure final state is correct
       */
        System.out.println("Final state: " + PrettyPrintState(_currentState) );

        assertEquals(POSTSHUTDOWN, _currentState);
    }

	@Test
	public void testOAShutdownCalled() throws Exception
	{
		ORB orb = ORB.getInstance("main_orb");
		RootOA oa = RootOA.getRootOA(orb);

		System.out.println("Initialising ORB and OA");

		orb.initORB(new String[] {}, null);
		oa.initOA();

		_currentState = NONE;

		/**
		 * Register pre and post shutdown handlers
		 */
		oa.addPreShutdown( new TestPreShutdown( "PreShutdown", this ) );
		oa.addPostShutdown( new TestPostShutdown( "PostShutdown", this ) );

		System.out.println("Shutting down ORB (expecting OA to also be destroyed)");
		orb.shutdown();

        /*
       * Ensure final state is correct
       */
		System.out.println("Final state: " + PrettyPrintState(_currentState) );

		assertEquals(POSTSHUTDOWN, _currentState);
	}

    /**
     *
     */
    public class TestPreShutdown extends com.arjuna.orbportability.oa.PreShutdown
    {
    	private PrePostTestCallback 	_callback;

    	public TestPreShutdown(String name, PrePostTestCallback callback)
    	{
    	    super(name);

    	    _callback = callback;
    	}

    	/**
    	 * Should be called before the OA is shutdown
    	 */
    	public void work ()
    	{
 	    _callback.preShutdownCalled(name());
	}
    }

    /**
     *
     */
    public class TestPostShutdown extends com.arjuna.orbportability.oa.PostShutdown
    {
    	private PrePostTestCallback 	_callback;

    	public TestPostShutdown(String name, PrePostTestCallback callback)
    	{
    	    super(name);

    	    _callback = callback;
    	}

    	/**
    	 * Should be called before the OA is shutdown
    	 */
    	public void work ()
    	{
 	    _callback.postShutdownCalled(name());
	}
    }
}