/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.vfs;

import java.util.List;
import java.util.Map;

import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface VFSService {

    Path get(final String first, final String... more)
            throws IllegalArgumentException;

    Path get(final Path path)
            throws IllegalArgumentException;

    //TODO for demo purpose ONLY
    DirectoryStream<Path> newDirectoryStream()
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir)
            throws IllegalArgumentException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir, String glob)
            throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException;

    DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter)
            throws IllegalArgumentException, NotDirectoryException, IOException;

    Path createFile(Path path, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectory(Path dir, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createDirectories(Path dir, FileAttribute<?>... attrs)
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException;

    Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    Path createLink(Path link, Path existing)
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException;

    void delete(Path path)
            throws IllegalArgumentException, NoSuchFileException,
            DirectoryNotEmptyException, IOException;

    boolean deleteIfExists(Path path)
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException;

    Path createTempFile(Path dir, String prefix,
            String suffix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path createTempDirectory(String prefix, FileAttribute<?>... attrs)
            throws IllegalArgumentException, UnsupportedOperationException, IOException;

    Path copy(Path source, Path target, CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException;

    Path move(Path source, Path target, CopyOption... options)
            throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException,
            AtomicMoveNotSupportedException, IOException;

    Path readSymbolicLink(Path link)
            throws IllegalArgumentException, UnsupportedOperationException,
            NotLinkException, IOException;

    String probeContentType(Path path)
            throws UnsupportedOperationException, IOException;

    //TODO out for now - errai has some issues with generics
//    <V extends FileAttributeView> V getFileAttributeView(Path path,
//            Class<V> type, LinkOption... options)
//            throws IllegalArgumentException;
//
//    <A extends BasicFileAttributes> A readAttributes(Path path,
//            Class<A> type)
//            throws IllegalArgumentException, UnsupportedOperationException, NoSuchFileException, IOException;

    Map<String, Object> readAttributes(Path path)
            throws UnsupportedOperationException, IllegalArgumentException, IOException;

    Path setAttribute(Path path, String attribute,
            Object value, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException,
            ClassCastException, IOException;

    Object getAttribute(Path path, String attribute, LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException;

    UserPrincipal getOwner(Path path, LinkOption... options)
            throws UnsupportedOperationException, IOException;

    Path setOwner(Path path, UserPrincipal owner)
            throws UnsupportedOperationException, IOException;

    Path setLastModifiedTime(Path path, FileTime time)
            throws IOException;

    long size(Path path)
            throws IllegalArgumentException, IOException;

    boolean notExists(Path path, LinkOption... options)
            throws IllegalArgumentException;

    boolean isSameFile(Path path, Path path2)
            throws IllegalArgumentException, IOException;

    boolean isExecutable(Path path)
            throws IllegalArgumentException;

    byte[] readAllBytes(Path path)
            throws IOException;

    String readAllString(Path path, String charset)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    String readAllString(Path path)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    List<String> readAllLines(Path path, String charset)
            throws IllegalArgumentException, NoSuchFileException, IOException;

    List<String> readAllLines(Path path)
            throws IllegalArgumentException, NoSuchFileException, IOException;

//    Path write(Path path, byte[] bytes)
//            throws IOException, UnsupportedOperationException;
//
//    Path write(Path path,
//            Iterable<? extends CharSequence> lines, String charset)
//            throws IllegalArgumentException, IOException, UnsupportedOperationException;
//
//    Path write(Path path,
//            Iterable<? extends CharSequence> lines)
//            throws IllegalArgumentException, IOException, UnsupportedOperationException;
//
//    Path write(Path path,
//            String content, String charset)
//            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    Path write(Path path,
            String content)
            throws IllegalArgumentException, IOException, UnsupportedOperationException;

    List<FileSystem> listJGitFileSystems();

    List<JGitRepositoryConfigurationVO> listJGitRepositories();

    JGitRepositoryConfigurationVO loadJGitRepository(String repositoryName);

    void createJGitFileSystem(String repositoryName, String description, String userName,
            String password) ;

    void cloneJGitFileSystem(String repositoryName, String gitURL,
            String userName, String password);

}
