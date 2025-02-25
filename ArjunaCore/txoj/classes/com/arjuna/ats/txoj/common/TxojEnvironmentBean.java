/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.txoj.common;

import java.io.File;

import com.arjuna.ats.internal.txoj.lockstore.BasicLockStore;
import com.arjuna.common.internal.util.propertyservice.PropertyPrefix;

/**
 * A JavaBean containing configuration properties for the transactional object system.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com)
 */
@PropertyPrefix(prefix = "com.arjuna.ats.txoj.lockstore.")
public class TxojEnvironmentBean implements TxojEnvironmentBeanMBean
{
    @Deprecated
    private volatile String lockStoreDir = System.getProperty("user.dir") + File.separator + "LockStore";
    private volatile String lockStoreType = BasicLockStore.class.getName();
    @Deprecated
    private volatile String multipleLockStore = null;
    @Deprecated
    private volatile String singleLockStore = BasicLockStore.class.getName();
    private volatile boolean allowNestedLocking = true;

    /**
     * Returns the directory path used for storing persistent locks.
     *
     * Default: {user.dir}/LockStore
     * Equivalent deprecated property: com.arjuna.ats.txoj.lockstore.lockStoreDir
     *
     * @return the path to the lock directory.
     */
    @Deprecated
    public String getLockStoreDir()
    {
        return lockStoreDir;
    }

    /**
     * Sets the directory path to be used for storing persistent locks.
     *
     * @param lockStoreDir the path to the lock directory.
     */
    @Deprecated
    public void setLockStoreDir(String lockStoreDir)
    {
        this.lockStoreDir = lockStoreDir;
    }

    /**
     * Returns the name of the lock store implementation.
     *
     * Default: null
     * Equivalent deprecated property: com.arjuna.ats.txoj.lockstore.lockStoreType
     *
     * @return the name of the lock store implementation.
     */
    
    public String getLockStoreType()
    {
        return lockStoreType;
    }

    /**
     * Sets the name of the lock store implementation.
     *
     * @param lockStoreType the name of the lock store implementation.
     */
    public void setLockStoreType(String lockStoreType)
    {
        this.lockStoreType = lockStoreType;
    }

    /**
     * Returns the name of the multiple lock store implementation.
     *
     * Default: null
     * Equivalent deprecated property: com.arjuna.ats.txoj.lockstore.multipleLockStore
     *
     * @return the name of the multiple lock store implementation. 
     */
    @Deprecated
    public String getMultipleLockStore()
    {
        return multipleLockStore;
    }

    /**
     * Sets the name of the multiple lock store implementation.
     *
     * @param multipleLockStore the name of the multiple lock store implementation.
     */
    @Deprecated
    public void setMultipleLockStore(String multipleLockStore)
    {
        this.multipleLockStore = multipleLockStore;
    }

    /**
     * Sets the name of the single lock store implementation.
     *
     * Default: "BasicLockStore" TODO test
     * Equivalent deprecated property: com.arjuna.ats.txoj.lockstore.singleLockStore
     *
     * @return the name of the single lock store implementation.
     */
    @Deprecated
    public String getSingleLockStore()
    {
        return singleLockStore;
    }

    /**
     * Sets the name of the single lock store implementation.
     *
     * @param singleLockStore  the name of the single lock store implementation.
     */
    @Deprecated
    public void setSingleLockStore(String singleLockStore)
    {
        this.singleLockStore = singleLockStore;
    }

    /**
     * Returns if nested locking is allowed or not.
     *
     * Default: true
     * Equivalent deprecated property: com.arjuna.ats.txoj.lockstore.allowNestedLocking
     *
     * @return true if nested locking is enabled, false otherwise.
     */
    public boolean isAllowNestedLocking()
    {
        return allowNestedLocking;
    }

    /**
     * Sets if nested locking is allowed or not.
     *
     * @param allowNestedLocking true to enable, false to disable.
     */
    public void setAllowNestedLocking(boolean allowNestedLocking)
    {
        this.allowNestedLocking = allowNestedLocking;
    }
}