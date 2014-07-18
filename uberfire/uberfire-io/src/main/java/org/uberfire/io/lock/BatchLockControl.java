package org.uberfire.io.lock;

import org.uberfire.java.nio.file.FileSystem;

public interface BatchLockControl {

    void start( FileSystem[] fileSystems );

    FileSystem[] getLockedFileSystems();

    void end();
}
