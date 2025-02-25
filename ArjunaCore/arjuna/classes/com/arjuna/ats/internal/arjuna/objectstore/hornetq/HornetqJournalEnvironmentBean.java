/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package com.arjuna.ats.internal.arjuna.objectstore.hornetq;

import java.io.File;

import com.arjuna.ats.arjuna.logging.tsLogger;
import com.arjuna.common.internal.util.propertyservice.PropertyPrefix;

/**
 * A JavaBean containing assorted configuration properties for the Journal based transaction logging system.
 *
 * Parameters on this file serve a similar role to their counterparts in the artemis journal.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com), 2010-03
 */
@PropertyPrefix(prefix = "com.arjuna.ats.arjuna.hornetqjournal.")
public class HornetqJournalEnvironmentBean implements HornetqJournalEnvironmentBeanMBean
{
    private volatile int fileSize = 1024*1024*2;

    private volatile int minFiles = 4;

    private volatile int poolSize = 20;

    private volatile int compactMinFiles = 10;

    private volatile int compactPercentage = 30;

    private volatile String filePrefix = "jbossts";

    private volatile String fileExtension = "txlog";

    private volatile int maxIO = 2;

    private volatile String storeDir = System.getProperty("user.dir") + File.separator + "HornetqJournalStore";

    private volatile boolean syncWrites = true;

    private volatile boolean syncDeletes = true;

    private volatile int bufferFlushesPerSecond = 500;

    private volatile int bufferSize = 490 * 1024;

    private volatile boolean logRates = false;

    private volatile boolean asyncIO = true;


    /**
     * Returns the desired size in bytes of each log file.
     * Minimum 1024.
     *
     * Default: 2MB (2097152 bytes)
     *
     * @return The individual log file size, in bytes.
     */
    public int getFileSize()
    {
        return fileSize;
    }

    /**
     * Sets the desired size in bytes for each log file.
     *
     * @param fileSize the individual log file size, in bytes.
     */
    public void setFileSize(int fileSize)
    {
        this.fileSize = fileSize;
    }

    /**
     * Returns the minimum number of log files to use.
     * Minimum 2.
     *
     * Default: 4
     *
     * @return the minimum number of individual log files.
     */
    public int getMinFiles()
    {
        return minFiles;
    }

    /**
     * Sets the minimum number of log files to use.
     *
     * @param minFiles the minimum number of individual log files.
     */
    public void setMinFiles(int minFiles)
    {
        this.minFiles = minFiles;
    }

    /**
     * How many journal files can be reused.
     *
     * Default: -1
     *
     * @return the number of files that can be reused.
     */
    public int getPoolSize()
    {
        return poolSize;
    }

    /**
     * Sets the number of files that can be reused.
     *
     * @param poolSize the number of files that can be reused.
     */
    public void setPoolSize(int poolSize)
    {
        this.poolSize = poolSize;
    }

    /**
     * Gets the minimal number of files before we can consider compacting.
     *
     * Default: 10
     *
     * @return the threshold file count.
     */
    public int getCompactMinFiles()
    {
        return compactMinFiles;
    }

    /**
     * Sets the minimal number of files before we can consider compacting.
     *
     * @param compactMinFiles the threshold file count.
     */
    public void setCompactMinFiles(int compactMinFiles)
    {
        this.compactMinFiles = compactMinFiles;
    }

    /**
     * Gets the percentage minimum capacity usage at which to start compacting.
     *
     * Default: 30
     *
     * @return the threshold percentage.
     */
    public int getCompactPercentage()
    {
        return compactPercentage;
    }

    /**
     * Sets the percentage minimum capacity usage at which to start compacting.
     *
     * @param compactPercentage the threshold percentage.
     */
    public void setCompactPercentage(int compactPercentage)
    {
        this.compactPercentage = compactPercentage;
    }

    /**
     * Returns the prefix to be used when naming each log file.
     *
     * Default: "jbossts"
     *
     * @return the prefix used to construct individual log file names.
     */
    public String getFilePrefix()
    {
        return filePrefix;
    }

    /**
     * Sets the prefix to be used when naming each log file.
     *
     * @param filePrefix the prefix used to construct individual log file names.
     */
    public void setFilePrefix(String filePrefix)
    {
        this.filePrefix = filePrefix;
    }

    /**
     * Returns the suffix to be used then naming each log file.
     *
     * Default: "txlog"
     *
     * @return the suffix used to construct individual log file names.
     */
    public String getFileExtension()
    {
        return fileExtension;
    }

    /**
     * Sets the suffix to be used when naming each log file.
     *
     * @param fileExtension the suffix used to construct individual log file names.
     */
    public void setFileExtension(String fileExtension)
    {
        this.fileExtension = fileExtension;
    }

    /**
     * Gets the maximum write requests queue depth.
     * For NIO this property has no effect and will be hard coded to 1.
     * For AIO, the default is 2 but the recommended value is 500.
     *
     * Default: 2
     *
     * @return the max number of outstanding requests.
     */
    public int getMaxIO()
    {
        return maxIO;
    }

    /**
     * Sets the maximum write requests queue depth.
     *
     * @param maxIO the max number of outstanding requests.
     */
    public void setMaxIO(int maxIO)
    {
        if (maxIO == 1 && isAsyncIO()) {
            // this logic is to workaround an artemis feature where maxIO = 1 isn't supported in AIO mode
            // the feature will be removed in version org.apache.activemq:artemis-journal:2.17.x or later
            // and then this workaround will be removed.
            tsLogger.i18NLogger.info_maxIO();
            this.maxIO = 2;
        } else {
            this.maxIO = maxIO;
        }
    }

    /**
     * Returns the log directory path
     *
     * Default: {user.dir}/HornetqJournalStore
     *
     * @return the log directory name
     */
    public String getStoreDir()
    {
        return storeDir;
    }

    /**
     * Sets the log directory path.
     *
     * @param storeDir the path to the log directory.
     */
    public void setStoreDir(String storeDir)
    {
        this.storeDir = storeDir;
    }

    /**
     * Returns the sync setting for transaction log write operations.
     * To preserve ACID properties this value must be set to true, in which case
     * log write operations block until data is forced to the physical storage device.
     * Turn sync off only if you don't care about data integrity.
     *
     * Default: true.
     *
     * @return true if log writes should be synchronous, false otherwise.
     */
    public boolean isSyncWrites()
    {
        return syncWrites;
    }

    /**
     * Sets if log write operations should be synchronous or not.
     *
     * @param syncWrites true for synchronous operation, false otherwise.
     */
    public void setSyncWrites(boolean syncWrites)
    {
        this.syncWrites = syncWrites;
    }

    /**
     * Returns the sync setting for transaction log delete operations.
     * For optimal crash recovery this value should be set to true.
     * Asynchronous deletes may give rise to unnecessary crash recovery complications.
     *
     * Default: true.
     * 
     * @return true if log deletes should be synchronous, false otherwise.
     */
    public boolean isSyncDeletes()
    {
        return syncDeletes;
    }

    /**
     * Sets if log delete operations should be synchronous or not.
     *
     * @param syncDeletes true for synchronous operation, false otherwise.
     */
    public void setSyncDeletes(boolean syncDeletes)
    {
        this.syncDeletes = syncDeletes;
    }

    /**
     * Returns the target number of timer based buffer flushes per second.
     * Caution: this property is functionally equivalent to the artemis
     * journal-buffer-timeout property but uses different units.
     *
     * Default 500.
     *
     * @return the number of buffer flushes per second.
     */
    public int getBufferFlushesPerSecond()
    {
        return bufferFlushesPerSecond;
    }

    /**
     * Sets the target number of timer based buffer flushes per second.
     *
     * @param bufferFlushesPerSecond the target number.
     */
    public void setBufferFlushesPerSecond(int bufferFlushesPerSecond)
    {
        this.bufferFlushesPerSecond = bufferFlushesPerSecond;
    }

    /**
     * Returns the buffer size in bytes.
     *
     * Default: 490 KB.
     *
     * @return the size of the buffer.
     */
    public int getBufferSize()
    {
        return bufferSize;
    }

    /**
     * Sets the buffer size in bytes.
     *
     * @param bufferSize the size of the buffer.
     */
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    /**
     * Returns the debug log mode for Journal throughput statistics.
     *
     * Default: false.
     *
     * @return true is rate logging is enabled, false otherwise.
     */
    public boolean isLogRates()
    {
        return logRates;
    }

    /**
     * Sets the debug log mode for Journal throughput statistics.
     *
     * @param logRates true to enable logging of statistics, false to disable.
     */
    public void setLogRates(boolean logRates)
    {
        this.logRates = logRates;
    }

    /**
     * Returns the IO type of Journal.
     *
     * Default: true
     *
     * @return true if AsyncIO is enabled, false otherwise which means NIO
     */
    public boolean isAsyncIO() {
        return asyncIO;
    }

    /**
     * Sets the type of Journal.
     *
     * <i>Note that Journal silently falls back to NIO if AIO native libraries are not available.</i>
     *
     * @param asyncIO true to enable AsyncIO, false to disable
     */
    public void setAsyncIO(boolean asyncIO) {
        if (maxIO == 1) {
            // this logic is to workaround an artemis feature where maxIO = 1 isn't supported in AIO mode,
            // the feature will be removed in version org.apache.activemq:artemis-journal:2.17.x or later
            // and then this workaround will be removed.
            tsLogger.i18NLogger.info_maxIO();
            this.maxIO = 2;
        }

        this.asyncIO = asyncIO;
    }
}