package org.uberfire.backend.vfs;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.java.nio.IOException;

/**
 * Provides methods to manage locks in UberFire's virtual file system.
 */
@Remote
public interface VFSLockService {

    /**
     * Creates a lock file for the specified {@link Path}, to be held by the
     * currently authenticated user. If successful, this method associates the
     * created lock with the user's HTTP session so locks can automatically be
     * released when the session ends, expires or is destroyed.
     * 
     * @param path
     *            the path of the file or directory to lock.
     * @return the {@link LockResult}, indicating success or failure and
     *         containing the last read {@link LockInfo}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be written or an existing lock can't be
     *             read.
     */
    LockResult acquireLock( Path path )
            throws IllegalArgumentException, IOException;

    /**
     * Deletes the lock file for the specified {@link Path}.
     * 
     * @param path
     *            the path of the file or directory currently assumed locked.
     * @return the {@link LockResult}, indicating success or failure and
     *         containing the last read {@link LockInfo}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be deleted or an existing lock can't be
     *             read.
     */
    LockResult releaseLock( Path path )
            throws IllegalArgumentException, IOException;

    /**
     * Retrieve the lock information for the specified {@link Path}.
     * 
     * @param path
     *            the path of the file or directory.
     * @return the {@link LockInfo} for the provided {@link Path}.
     * 
     * @throws IllegalArgumentException
     *             If the provided path is invalid or null.
     * @throws IOException
     *             If a lock file can't be deleted or an existing lock can't be
     *             read.
     */
    LockInfo retrieveLockInfo( Path path )
            throws IllegalArgumentException, IOException;
}
