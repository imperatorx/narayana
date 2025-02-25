/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.recovery ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.utils.Utility;

public class TransactionStatusConnector
{
   /**
    * Recreate TransactionStatusManagerItem and attempt to establish
    * a connection to the host/port of the TransactionStatusManager.
    */

   public TransactionStatusConnector( String pid, Uid pidUid )
   {
      _pid    = pid ;
      _pidUid = pidUid ;
   }
   
   /**
    * Check that a connection can be made to the Transaction Status
    * Manager's process.
    */

   public boolean test( TransactionStatusManagerItem tsmi )
   {
      _testMode = true ;
      _tsmi = tsmi ;

      boolean ok = establishConnection() ;
      
      if ( !ok )
      {
         setDeadTSM() ;
      }

      return ok ;
   }

   /**
    * If the TransactonStatusManagers' process is deemed dead,
    * then its TransactonStatusManagerItem is removed from
    * the object store.
    */

   public void delete ()
   {
      if ( _dead )
      {
         TransactionStatusManagerItem.removeThis( _pidUid ) ;
      }
      else {
          tsLogger.i18NLogger.warn_recovery_TransactionStatusConnector_1();
      }
   }
   
   /**
    * Has the TransactionStatusManagers' process died.
    */

   public boolean isDead ()
   {
      return _dead ;
   }
   
   /**
    * Retrieve the transaction status for the specified transaction,
    * attempts to re-establish connection if necessary.
    */

   public int getTransactionStatus ( String transaction_type, Uid tranUid )
   {
      int status = ActionStatus.INVALID ;

      if ( ! _dead )
      {
         if ( ! _tsmFound )
         {
            // try to establish/re-establish the connection
            _tsmFound = recreateTransactionStatusManagerItem() ;
            
            if ( _tsmFound )
            {
               _tsmFound = establishConnection() ;
            }
         }

         if ( _tsmFound )
         {
            try
            {
               // Send transaction type and transaction Uid to the
               // TransactionStatusManager.

               _to_server.println ( transaction_type ) ;
               _to_server.println ( tranUid.toString() ) ;
               _to_server.flush() ;

	       /*
		* TODO we should optimise this so we only close once
		* all transactions for a particular host have been sent.
		*/

               // Retrieve current status from the TransactionStatusManager.
               String server_data = _from_server.readLine() ;
               status = Integer.parseInt ( server_data ) ;

	       //	       _to_server.close();
	       //	       _from_server.close();
            }
            catch ( IOException ex ) {
                tsLogger.i18NLogger.warn_recovery_TransactionStatusConnector_2();

                _tsmFound = false;
            }
            catch ( Exception other ) {
                tsLogger.i18NLogger.warn_recovery_TransactionStatusConnector_3();

                _tsmFound = false;
            }
         }
      }

      return status ;
   }
   
   /**
    * Assume the Transaction Status Managers' process has died.
    */
   private void setDeadTSM()
   {
      _dead = true ;

      if (_tsmi != null)
	  _tsmi.markAsDead();
   }

   /**
    * Create socket and input/output streams to/from the
    * TransactionStatusManager.
    */

   private boolean establishConnection()
   {
      boolean connectionEstablished = false ;
      
      if ( _tsmi != null )
      {
         try
         {
            String serverHost = _tsmi.host() ;
            int serverPort = _tsmi.port() ;

            _connector_socket = new Socket ( serverHost, serverPort ) ;
            _connector_socket.setSoTimeout ( _socket_timeout_in_msecs ) ;
   
            // streams to and from the TransactionStatusManager
            _from_server = new BufferedReader ( new InputStreamReader( _connector_socket.getInputStream(), StandardCharsets.UTF_8 )) ;
                              
            _to_server = new PrintWriter ( new OutputStreamWriter( _connector_socket.getOutputStream(), StandardCharsets.UTF_8 ) ) ;

            // Check that the process id of the server is the same as
            // this connectors process id.

            String server_pid = _from_server.readLine() ;
          
            if ( Utility.hexStringToInt(server_pid) == Utility.hexStringToInt(_pid) )
            {
               if ( ! _testMode )
               {
                  _to_server.println ( "OK" ) ;
                  _to_server.flush() ;

                   tsLogger.i18NLogger.info_recovery_TransactionStatusConnector_4(_pid, serverHost, Integer.toString(serverPort), _connector_socket.toString());
               }
               else
               {
                  _to_server.println ( "TEST" ) ;
                  _to_server.flush() ;
		  //		  _to_server.close();

                  _connector_socket.close() ;
               }
               
               connectionEstablished = true ;
            }
            else
            {
               _to_server.println ( "DEAD" ) ;
               _to_server.flush() ;
	       //	       _to_server.close();

               _connector_socket.close() ;
               
               setDeadTSM() ;
	       
       	       tsLogger.i18NLogger.info_recovery_TransactionStatusConnector_5(_pid);
	       
            }
         }
         catch ( IOException ex )
         {
	        tsLogger.i18NLogger.info_recovery_TransactionStatusConnector_6();
         }

         _attempts_to_establish_connection = connectionEstablished ? 0 : _attempts_to_establish_connection + 1 ;
         
         if ( _attempts_to_establish_connection > _max_attempts_to_establish_connection )
         {
            setDeadTSM() ;
         }
      }
      
      return connectionEstablished ;
   }

   /**
    * Retrieve host/port item stored in the object store.
    */
   private boolean recreateTransactionStatusManagerItem()
   {
      boolean tsmiFound = false ;
      
      if ( _tsmi == null )
      {
         try
         {
            _tsmi = TransactionStatusManagerItem.recreate( _pidUid ) ;

            tsmiFound = true ;
            _attempts_to_recreate_tsmi = 0 ;
         }
         catch ( Exception ex )
         {
            if ( ++_attempts_to_recreate_tsmi > _max_attempts_to_recreate_tsmi )
            {
               setDeadTSM() ;
            }

            _tsmi = null ;
         }
      }

      return tsmiFound ;
   }

   // Process id & process Uid.
   private String _pid ;        // HexString format
   private Uid    _pidUid ;
   
   // Host/port pair for TransactionStatusManager.
   private TransactionStatusManagerItem _tsmi = null ;
   
   // If transaction status manager item exists AND able to 
   // connect to its host/port then _tsmFound = true.
   private boolean _tsmFound = false ;
   
   // Several attempts are made to recreate _tsmi,
   // if limit reached then this connector is marked dead.
   private int _attempts_to_recreate_tsmi = 0 ;
   private int _max_attempts_to_recreate_tsmi = 3 ;
   
   // Several attempts are made to establish a connection to the
   // Transaction Status Manager, if limit reached then this connector
   // is marked dead.
   private int _attempts_to_establish_connection = 0 ;
   private int _max_attempts_to_establish_connection = 3 ;
   
   // Socket to connect to host/port pair maintained in _tsmi.
   private Socket _connector_socket ;
   private int    _socket_timeout_in_msecs = 1000 ;
   
   // IO to/from TransactionStatusManager
   private BufferedReader _from_server;
   private PrintWriter    _to_server;
   
   // Indicates the TransactionStatusManagers' process does not exist.
   private boolean _dead = false ;
   
   // Used to check that a connection can be established to the
   // TransactionStatusManagers' process.
   private boolean _testMode = false ;

}