/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */



package com.arjuna.ats.internal.arjuna.recovery ;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import com.arjuna.ats.arjuna.common.Uid;
import com.arjuna.ats.arjuna.exceptions.ObjectStoreException;
import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.ats.arjuna.objectstore.ParticipantStore;
import com.arjuna.ats.arjuna.objectstore.StoreManager;
import com.arjuna.ats.arjuna.state.InputObjectState;
import com.arjuna.ats.arjuna.state.OutputObjectState;
import com.arjuna.ats.arjuna.utils.Utility;

// similar to FactoryContactItem
public class TransactionStatusManagerItem
{
    /**
     * Create the instance of a Transaction Status Manager
     * contact item.
     * @deprecated Only used in tests
     */
    public static boolean createAndSave( int port )
    {
	boolean ret_status = true ;
	
	if ( _singularItem == null )
	    {
		_singularItem = new TransactionStatusManagerItem( port );
		
		ret_status = _singularItem.saveThis();
	    }
	return ret_status ;
    }

    public static boolean createAndSave(String hostAddress, int port )
    {
        boolean ret_status = true ;

        if ( _singularItem == null )
        {
            _singularItem = new TransactionStatusManagerItem(hostAddress, port );

            ret_status = _singularItem.saveThis();
        }
        return ret_status ;
    }

    /**
     * Get a reference to the Object Store.
     */
    private static ParticipantStore getStore()
    {
        return StoreManager.getCommunicationStore();
    }
    
    /**
     * Accessor method for host in format xxx.xxx.xxx.xxx
     */
    public String host()
    {
	return _host ;
    }
    
    /**
     * Accessor method for the port used by this object.
     */
    public int port()
    {
	return _port ;
    }
    
    /**
     * The process has died.
     */
    public void markAsDead()
    {
	// ignore if done previously
	if ( ! _markedDead )
	    {
		// the host/port won't work any more, so forget it
		_markedDead = true ;
		_deadTime = new Date() ;
		saveThis() ;
	    }
    }
    
    /**
     * Return time when process marked dead.
     */ 
    public Date getDeadTime()
    {
	return _deadTime ;
    }
    
    /**
     * Returns reference to this transaction status manager item.
     */   
    public static TransactionStatusManagerItem get()
    {
	return _singularItem ;
    }
    
    /**
    * Crash Recovery uses this method to recreate a
    * representation of the Transaction Status Managers
    * host/port pair contact.
    */
    public static TransactionStatusManagerItem recreate ( Uid uid )
    {
	TransactionStatusManagerItem 
	    theItem = new TransactionStatusManagerItem( uid ) ;
	
	if ( theItem.restoreThis() )
	    {
		return theItem ;
	    }
	else
	    {
         return null;
	    }
    }
    
    /**
     * Destroy the host/port pair for the specified process Uid.
     */ 
    public static boolean removeThis( Uid pidUid )
    {
	boolean ret_status = false ;
	
      try
	  {
	      ret_status = getStore().remove_committed( pidUid, _typeName ) ;
	  }
      catch ( ObjectStoreException ex ) {
          tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_1(ex);
      }
      
      return ret_status ;
    }
    
    /**
     * Type used as path into object store for a TransactionStatusManagerItem.
     */
    public static String typeName()
    {
	return _typeName ;
   }
    
    /**
     * Read host/port pair from the ObjectStore using
     * the process Uid as a unique identifier.
     */
    private boolean restoreThis()
   {
       boolean ret_status = false ;
       
       try
      { 
	  InputObjectState objstate = getStore().read_committed( _pidUid,
								 _typeName ) ;
	  
	  if ( restore_state( objstate) )
	      {
            return ret_status = true ;
	      }
      }
       catch ( ObjectStoreException ex ) {
           ret_status = false;

           tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_2(ex);
       }
       
       return ret_status ;
   }
    
    /**
     * Retrieve host/port pair from the Object Store.
    */
    private boolean restore_state ( InputObjectState objstate )
    {
	boolean ret_status = false ;
	
	try
        {
	    _host = objstate.unpackString() ;
	    _port = objstate.unpackInt() ;
	    _markedDead = objstate.unpackBoolean() ;
	    
	    if ( _markedDead )
		{
		    long deadtime = objstate.unpackLong() ;
		    _deadTime = new Date( deadtime ) ;
		}  
            
	    ret_status = true ;
	}
	catch ( IOException ex ) {
        tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_3(ex);
    }
	
	return ret_status ;
    }
   
    /**
     * Save host/port pair to the Object Store.
     */
    private boolean save_state ( OutputObjectState objstate )
    {
	boolean ret_status = false ;
	
	try
	    {
		objstate.packString( _host ) ;
		objstate.packInt( _port ) ;
		
		objstate.packBoolean( _markedDead ) ;
		
		if ( _markedDead )
		    {
            objstate.packLong( _deadTime.getTime() ) ;
		    }
		
		ret_status = true ;
	    }
	catch ( IOException ex ) {
        tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_2(ex);
    }
	
	return ret_status ;
    }
    
    /**
     * Write host/port pair to the ObjectStore using
     * the process Uid as a unique identifier.
     */
   private boolean saveThis()
    {
	boolean ret_status = false ;
	
	try
	    {
		OutputObjectState objstate = new OutputObjectState();
		
		if ( save_state(objstate) )
		    {
			ret_status = getStore().write_committed ( _pidUid, 
								  _typeName, 
								  objstate ) ;
         }
	    }
	catch ( ObjectStoreException ex ) {
        tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_2(ex);
    }
	
	return ret_status ;
   }
    
    /**
     * Constructor which obtains the process uid and host for
     * use with the specified port.
     */
   private TransactionStatusManagerItem ( int port )
    {
	_pidUid = Utility.getProcessUid() ;
	_port = port ;
	
	try
	{
	    _host = InetAddress.getLocalHost().getHostAddress() ;
         
	    tsLogger.logger.debugf("TransactionStatusManagerItem host: {0} port: {1}", _host, Integer.toString(_port));
	}
	catch ( UnknownHostException ex ) {
        tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_4(ex);
    }
    }

    /**
     * Constructor which obtains the process uid for
     * use with the specified host and port.
     */
    private TransactionStatusManagerItem (String host, int port )
    {
        _pidUid = Utility.getProcessUid() ;
        _port = port ;

        try
        {
            // make sure the passed in host is valid
            Utility.hostNameToInetAddress(host);
            _host = host;

            tsLogger.logger.debugf("TransactionStatusManagerItem host: {0} port: {1}", _host, Integer.toString(_port));
        }
        catch ( UnknownHostException ex ) {
            tsLogger.i18NLogger.warn_recovery_TransactionStatusManagerItem_4(ex);
        }
    }

    /**
     * Used by a Recovery Manager to recreate a Transaction
    * status manager contact item.
    */
    private TransactionStatusManagerItem( Uid uid )
    {
	_pidUid = new Uid( uid ) ;
    }
    
    // Process Uid.
    private Uid _pidUid ;
   
    // Relative location in object store for this 'type'.
    private static String _typeName = "/Recovery/TransactionStatusManager" ;
    
    // Host/port pair on which to connect to the Transaction status manager.
    private String _host ;
    private int    _port ;
    
    // The singleton instance of this class.
    private static TransactionStatusManagerItem _singularItem = null ;
    
    // Time at which the process for this item has died.
    private Date _deadTime = null ;
    
    // flag indicates dead TSM
    private boolean _markedDead = false ;

}