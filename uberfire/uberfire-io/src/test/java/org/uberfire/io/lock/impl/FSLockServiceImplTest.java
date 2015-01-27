package org.uberfire.io.lock.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.io.CommonIOServiceDotFileTest;
import org.uberfire.io.IOService;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.io.lock.FSLockService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static java.lang.Thread.*;
import static org.junit.Assert.*;

public class FSLockServiceImplTest {

    final static IOService ioService = new IOServiceDotFileImpl();
    static FileSystem fs1;
    static FileSystem fs2;
    FSLockServiceImpl lockService = new FSLockServiceImpl();
    private static File path = null;

    @BeforeClass
    public static void setup() throws IOException {
        path = CommonIOServiceDotFileTest.createTempDirectory();
        System.setProperty( "org.uberfire.nio.git.dir", path.getAbsolutePath() );
        System.out.println( ".niogit: " + path.getAbsolutePath() );

        final URI newRepo = URI.create( "git://fs-lock-repo" );

        fs1 = ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
        Path init = ioService.get( URI.create( "git://fs-lock-repo/init.file" ) );
        ioService.write( init, "setupFS!" );

        final URI newRepo2 = URI.create( "git://fs-lock-repo-another-test" );

        fs2 = ioService.newFileSystem( newRepo2, new HashMap<String, Object>() {{
            put( "init", "true" );
        }} );
        init = ioService.get( URI.create( "git://fs-lock-repo/init.file" ) );
        ioService.write( init, "setupFS!" );
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        if ( path != null ) {
            FileUtils.deleteQuietly( path );
        }
    }

    @Test
    public void acquireLock() throws Exception {
        assertFalse( lockService.isLocked( fs1 ) );
        lockService.lock( fs1 );
        assertTrue( lockService.isLocked( fs1 ) );
        lockService.unlock( fs1 );
        assertFalse( lockService.isLocked( fs1 ) );
    }

    @Test
    public void acquireTwoLock() throws Exception {
        assertFalse( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs2 ) );
        lockService.lock( fs1 );
        assertTrue( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs2 ) );
        lockService.lock( fs2 );
        assertTrue( lockService.isLocked( fs1 ) );
        assertTrue( lockService.isLocked( fs2 ) );
        lockService.unlock( fs2 );
        assertFalse( lockService.isLocked( fs2 ) );
        assertTrue( lockService.isLocked( fs1 ) );
        lockService.unlock( fs1 );
        assertFalse( lockService.isLocked( fs1 ) );
        assertFalse( lockService.isLocked( fs1 ) );
    }

    @Test
    public void sameThreadShouldNotWaitForLock() {
        lockService.lock( fs1 );
        lockService.waitForUnlock( fs1 );

    }
}
