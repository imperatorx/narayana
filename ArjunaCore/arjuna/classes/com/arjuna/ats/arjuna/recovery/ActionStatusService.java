/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.arjuna.recovery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionManager;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.coordinator.BasicAction;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.RecoveryStore;
import com.arjuna.ats.arjuna.objectstore.StateStatus;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.utils.Utility;
import com.arjuna.ats.internal.arjuna.common.UidHelper;

public class ActionStatusService implements Service
{
   /**
    * Get a reference to the Transaction Store.
    */
   public ActionStatusService()
   {
      if ( _recoveryStore == null )
      {
         _recoveryStore = StoreManager.getRecoveryStore();
      }
   }

   /**
    * Retrieve the transaction status for the specified Uid and
    * if available transaction type.
    */

   public int getTransactionStatus( String transactionType, String strUid )
   {
      int action_status = ActionStatus.INVALID;

      if (strUid != null)
      {
	  Uid tranUid = new Uid( strUid );

	  if ( transactionType == null || transactionType.equals("") )
	  {
	      action_status = getTranStatus( tranUid );
	  }
	  else
	  {
              action_status = getActionStatus( tranUid, transactionType );
	  }
      }

      return action_status;
   }

   /**
    * Does the main work of reading in a uid and transaction type
    * from the recovery manager, retrieving the status of the
    * transaction and sending it back to the Recovery Manager.
    */

   public void doWork( InputStream is, OutputStream os )
      throws IOException
   {
      BufferedReader in  = new BufferedReader ( new InputStreamReader(is, StandardCharsets.UTF_8) );
      PrintWriter    out = new PrintWriter ( new OutputStreamWriter(os, StandardCharsets.UTF_8) );

      try
      {
         // Send the process id to the recovery module so that it
         // can verify that it is talking to the right process.
         out.println ( Utility.intToHexString( Utility.getpid() ));
         out.flush();

         // recovery module returns either "OK" or "DEAD"
         String rmStatus = in.readLine();

         if ( rmStatus.equals( "OK" ) )
         {
            for (;;)
            {
               // read in a transaction type and its Uid sent by the
               // recovery module.

               String transactionType = null;
               String strUid = null;

	       try
	       {
		   transactionType = in.readLine();
		   strUid = in.readLine();
	       }
	       catch (IOException ex)
	       {
		   // recovery manager has torn down connection, so end loop
	       }

	       /*
	        * check for null - in theory we get this from readLine when EOF has been reached, although in practice
	        * since we are reading from a socket we will probably get an IOException in which case we will still
	        * see null
		    */

	       if ((transactionType == null) && (strUid == null))
		   return;

               int status = getTransactionStatus( transactionType, strUid );
               String strStatus = Integer.toString( status );

               out.println( strStatus );
               out.flush();

	           tsLogger.i18NLogger.info_recovery_ActionStatusService_1(transactionType, strUid, strStatus);

	    }
         }
      }
      catch ( IOException ex ) {
          tsLogger.i18NLogger.warn_recovery_ActionStatusService_7();
      }
      catch ( Exception ex ) {
          tsLogger.i18NLogger.warn_recovery_ActionStatusService_2(ex);
      }
   }

    /**
     * Check for transaction status in the local hash table,
     * if does not exist, then retrieve the status from the
     * Object Store.
     */
    private int getActionStatus( Uid tranUid, String transactionType )
    {
	int action_status = ActionStatus.INVALID;

	try
	{
	    // check in local hash table
	    BasicAction basic_action = null;

	    synchronized ( ActionManager.manager() )
	    {
		basic_action = (BasicAction)ActionManager.manager().get( tranUid );
	    }

	    if ( basic_action != null)
	    {
		action_status = basic_action.status();
	    }
	    else
	    {
		/*
		 * If there is a persistent representation for this
		 * transaction, then return that status.
		 */
		action_status = getObjectStoreStatus( tranUid, transactionType );
	    }
	}
	catch ( Exception ex ) {
        tsLogger.i18NLogger.warn_recovery_ActionStatusService_3(ex);
    }

	return action_status;
    }

   /**
    * Get transaction status for a transaction when the transactionType
    * is unknown.
    */

   private int getTranStatus( Uid tranUid )
   {
      int action_status = ActionStatus.INVALID;

      try
      {
         BasicAction basic_action = null;

	 synchronized ( ActionManager.manager() )
         {
            basic_action = (BasicAction)ActionManager.manager().get( tranUid );
         }

         if ( basic_action != null)
         {
            action_status = basic_action.status();
         }
         else
         {
            /**
             * Run through the object store and try and find the matching id.
             */
            action_status = getOsStatus( tranUid );
         }
      }
      catch ( Exception ex ) {
          tsLogger.i18NLogger.warn_recovery_ActionStatusService_3(ex);
      }

      return action_status;
   }

   /**
    * Obtains the status for the specified transaction Uid when
    * the transaction type is unknown.
    */

   private int getOsStatus( Uid tranUid )
   {
      int action_status = ActionStatus.INVALID;

      Vector matchingUidVector = new Vector();
      Vector matchingUidTypeVector = new Vector();

      try
      {
         InputObjectState types = new InputObjectState();

         // find all types
         if ( _recoveryStore.allTypes(types) )
         {
            String theTypeName = null;

               boolean endOfList = false;

               while ( !endOfList )
               {
                  // extract a type
                  theTypeName = types.unpackString();

                  if ( theTypeName.compareTo("") == 0 )
                  {
                     endOfList = true;
                  }
                  else
                  {
                     InputObjectState uids = new InputObjectState();

                        boolean endOfUids = false;

                        if ( _recoveryStore.allObjUids( theTypeName, uids ) )
                        {
                           Uid theUid = null;

                           while ( !endOfUids )
                           {
                              // extract a uid
                               theUid = UidHelper.unpackFrom(uids);

                              if (theUid.equals( Uid.nullUid() ))
                              {
                                 endOfUids = true;
                              }
                              else if ( theUid.equals( tranUid ) )
                              {
                                 // add to vector
                                 matchingUidVector.addElement( tranUid );
                                 matchingUidTypeVector.addElement( theTypeName );
                                 tsLogger.i18NLogger.info_recovery_ActionStatusService_4(tranUid);
			      }
                           }
                        } else {
                        	return action_status; // Errors contacting recovery store for the list of uids it has for a type so return INVALID state
                        }
                  }
               }
         } else {
        	 return action_status; // Errors contacting recovery store for the list of types it holds so return INVALID state
         }
      }
      catch ( Exception ex ) {
          tsLogger.i18NLogger.warn_recovery_ActionStatusService_5(tranUid, ex);
          return action_status; // Read invalid data from the objectstore so return INVALID state
      }

      int uidVectorSize = matchingUidVector.size();
      int first_index = 0;

      if ( uidVectorSize == 0 )
      {
         // no state means aborted because of presumed abort rules
         action_status = ActionStatus.ABORTED;
      }
      else if ( uidVectorSize == 1 )
      {
         Uid uid = (Uid)matchingUidVector.get( first_index );
         String typeName = (String)matchingUidTypeVector.get( first_index );

         action_status = getObjectStoreStatus( uid, typeName );
      }

      else if ( uidVectorSize > 1 )
      {
         // find root of hierarchy
         Uid rootUid = (Uid)matchingUidVector.get( first_index );
         String rootTypeName = (String)matchingUidTypeVector.get( first_index );

         for ( int index = first_index+1; index < uidVectorSize; index++ )
         {
            String typeName = (String)matchingUidTypeVector.get( index );
            if ( typeName.length() < rootTypeName.length() )
            {
               rootTypeName = typeName;
               rootUid = (Uid)matchingUidVector.get( index );
            }
         }

         action_status = getObjectStoreStatus( rootUid, rootTypeName );
      }

      return action_status;
   }

   /**
    * Retrieve the status of the transaction from the object store.
    */
   private int getObjectStoreStatus( Uid tranUid, String transactionType )
   {
      int action_status = ActionStatus.INVALID;

      try
      {
         int osState = _recoveryStore.currentState( tranUid, transactionType );

         switch ( osState )
         {
	 case StateStatus.OS_COMMITTED :
	     action_status = ActionStatus.COMMITTED;
	     break;
	 case StateStatus.OS_UNKNOWN:
	     action_status = ActionStatus.ABORTED;  // no state means aborted because of presumed abort rules
	     break;
	 case StateStatus.OS_UNCOMMITTED        :
	 case StateStatus.OS_HIDDEN             :
	 case StateStatus.OS_COMMITTED_HIDDEN   :
	 case StateStatus.OS_UNCOMMITTED_HIDDEN :
	     action_status = ActionStatus.PREPARED;
	     break;
         }
      }
      catch ( Exception ex ) {
          tsLogger.i18NLogger.warn_recovery_ActionStatusService_6(ex);
      }

      return action_status;
   }

   /**
    * Reference to transaction object store.
    */

   private static RecoveryStore _recoveryStore = null;
}