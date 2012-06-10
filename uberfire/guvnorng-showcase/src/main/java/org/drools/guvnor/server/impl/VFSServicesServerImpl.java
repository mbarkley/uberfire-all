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

package org.drools.guvnor.server.impl;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.drools.guvnor.vfs.VFSTempUtil;
import org.drools.guvnor.vfs.impl.DirectoryStreamImpl;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.IOException;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Paths;
import org.drools.java.nio.file.PatternSyntaxException;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileTime;
import org.drools.java.nio.file.attribute.UserPrincipal;
import org.drools.java.nio.fs.base.GeneralPathImpl;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.server.annotations.Service;

import com.google.gwt.user.client.ui.TreeItem;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @Override
    public Path get(final String first, final String... more) throws IllegalArgumentException {
        return convert(Paths.get(first, more));
    }

    @Override
    public Path get(final Path path) throws IllegalArgumentException {
        return convert(Paths.get(URI.create(path.toURI())));
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream()
            throws IllegalArgumentException, NotDirectoryException, IOException {
        Path p = new PathImpl("");        

        return newDirectoryStream(Files.newDirectoryStream(fromPath(p)).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir) throws IllegalArgumentException, NotDirectoryException, IOException {
        return newDirectoryStream(Files.newDirectoryStream(fromPath(dir)).iterator());
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, String glob) throws IllegalArgumentException, UnsupportedOperationException, PatternSyntaxException, NotDirectoryException, IOException {
        return null;
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IllegalArgumentException, NotDirectoryException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createFile(Path path, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectory(Path dir, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createDirectories(Path dir, FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createLink(Path link, Path existing) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(Path path) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean deleteIfExists(Path path) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(Path dir, String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempFile(String prefix, String suffix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(Path dir, String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path createTempDirectory(String prefix, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path copy(Path source, Path target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path move(Path source, Path target, CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path readSymbolicLink(Path link) throws IllegalArgumentException, UnsupportedOperationException, NotLinkException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String probeContentType(Path path) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> readAttributes(final Path path) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return Files.readAttributes(fromPath(path), "*", null);
    }

    @Override
    public Path setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(Path path, String attribute, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipal getOwner(Path path, LinkOption... options) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setOwner(Path path, UserPrincipal owner) throws UnsupportedOperationException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path setLastModifiedTime(Path path, FileTime time) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long size(Path path) throws IllegalArgumentException, IOException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean notExists(Path path, LinkOption... options) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IllegalArgumentException, IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isExecutable(Path path) throws IllegalArgumentException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] readAllBytes(Path path) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String readAllString(Path path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, Charset.forName(charset));
    }

    @Override
    public String readAllString(final Path path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return readAllString(path, UTF_8);
    }

    private String readAllString(final Path path, final Charset cs)
            throws IllegalArgumentException, NoSuchFileException, IOException {

        final List<String> result = Files.readAllLines(fromPath(path), cs);
        if (result == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : result) {
            sb.append(s).append('\n');
        }
        return sb.toString();

    }

    @Override
    public List<String> readAllLines(Path path, String charset) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> readAllLines(Path path) throws IllegalArgumentException, NoSuchFileException, IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(Path path, byte[] bytes, OpenOption... options) throws IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, String charset, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path write(Path path, String content, String charset, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert(Files.write(fromPath(path), content, Charset.forName(charset), options));
    }

    @Override
    public Path write(Path path, String content, OpenOption... options) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert(Files.write(fromPath(path), content, UTF_8, options));
    }

    private Path convert(final org.drools.java.nio.file.Path path) {
        final Map<String, Object> attributes = Files.readAttributes(path, "*");

        //REVISIT - JLIU: Path.toUri constructs an absolute URI with a scheme equal to the URI scheme that identifies the provider. In order to support
        //relative path, shall we avoid using Path.toUri? or shall we always use absolute path?
        return new PathImpl(path.getFileName().toString(), path.toString(), attributes);
        //return new PathImpl(path.getFileName().toString(), path.toUri().toString(), attributes);
    }

    private DirectoryStream<Path> newDirectoryStream(final Iterator<org.drools.java.nio.file.Path> iterator) {
        final List<Path> content = new LinkedList<Path>();
        while (iterator.hasNext()) {
            content.add(convert(iterator.next()));
        }
        return new DirectoryStreamImpl(content);
    }

    private org.drools.java.nio.file.Path fromPath(final Path path) {
        return Paths.get(URI.create(path.toURI()));
    }
    
    public static void main(String[] args) throws Exception {
        VFSServicesServerImpl vfs = new VFSServicesServerImpl();
        //root - to JGIT, this means the root directory of the selected git repository. 
        URI u = new URI("");
        URI u2 = new URI("default:///.");
        URI u3 = URI.create("default:///.");
        
        URI u4 = new URI(null, null, ".", null, null);
        
        Path p = new PathImpl("");        
        DirectoryStream<Path> response = vfs.newDirectoryStream(p);
        
        for (final Path path : response) {
            Map<String, Object> attributes = vfs.readAttributes(path);

            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes(attributes);
            System.out.println("path.getFileName() " + path.getFileName());
            System.out.println("attrs.isDirectory() " + attrs.isDirectory());
            System.out.println("attrs.isRegularFile() " + attrs.isRegularFile());
            System.out.println("path.toURI() " + path.toURI());
        }
    }
}
