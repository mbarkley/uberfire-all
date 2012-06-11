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

package org.drools.java.nio.fs.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.drools.java.nio.IOException;
import org.drools.java.nio.channels.AsynchronousFileChannel;
import org.drools.java.nio.channels.SeekableByteChannel;
import org.drools.java.nio.file.AccessDeniedException;
import org.drools.java.nio.file.AccessMode;
import org.drools.java.nio.file.AtomicMoveNotSupportedException;
import org.drools.java.nio.file.CopyOption;
import org.drools.java.nio.file.DirectoryNotEmptyException;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.FileAlreadyExistsException;
import org.drools.java.nio.file.FileStore;
import org.drools.java.nio.file.FileSystem;
import org.drools.java.nio.file.FileSystemAlreadyExistsException;
import org.drools.java.nio.file.FileSystemNotFoundException;
import org.drools.java.nio.file.LinkOption;
import org.drools.java.nio.file.NoSuchFileException;
import org.drools.java.nio.file.NotDirectoryException;
import org.drools.java.nio.file.NotLinkException;
import org.drools.java.nio.file.OpenOption;
import org.drools.java.nio.file.Path;
import org.drools.java.nio.file.attribute.BasicFileAttributeView;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.drools.java.nio.file.attribute.FileAttribute;
import org.drools.java.nio.file.attribute.FileAttributeView;
import org.drools.java.nio.file.spi.FileSystemProvider;
import org.drools.java.nio.fs.base.GeneralPathImpl;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;

import com.gitblit.FileSettings;
import com.gitblit.GitBlit;
import com.gitblit.GitBlitException;
import com.gitblit.models.PathModel;
import com.gitblit.models.RepositoryModel;
import com.gitblit.utils.JGitUtils;

import static org.drools.java.nio.util.Preconditions.*;

public class JGitFileSystemProvider implements FileSystemProvider {

    private final JGitFileSystem fileSystem;
    private boolean isDefault;
    
    public static final String REPOSITORIES_ROOT_DIR = "./git";
    private Repository repository;

    public JGitFileSystemProvider() {
        setUpGitRepository();
        this.fileSystem = new JGitFileSystem(this);
    }
    
    public Repository getRepository() {
        return repository;
    }
    

    @Override
    public synchronized void forceAsDefault() {
        this.isDefault = true;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }

    @Override public String getScheme() {
        return "file";
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IllegalArgumentException, IOException, SecurityException, FileSystemAlreadyExistsException {
        return null;
    }

    @Override
    public FileSystem getFileSystem(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return fileSystem;
    }

    @Override
    public Path getPath(final URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
        return GeneralPathImpl.create(getDefaultFileSystem(), uri.getPath(), false);
    }

    @Override
    public FileSystem newFileSystem(final Path path, final Map<String, ?> env) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;
    }

    @Override
    public InputStream newInputStream(final Path path, final OpenOption... options)
            throws IllegalArgumentException, NoSuchFileException, IOException, SecurityException {        
        //TODO: check if the path specified exists in git repo
        
        byte[] byteContent = JGitUtils.getByteContent(repository, null, path.toString());
        return new ByteArrayInputStream(byteContent);
/*        
        final File file = path.toFile();
        if (!file.exists()) {
            throw new NoSuchFileException(file.toString());
        }
        try {
            return new FileInputStream(path.toFile());
        } catch (FileNotFoundException e) {
            throw new NoSuchFileException(e.getMessage());
        }*/
    }

    @Override
    public OutputStream newOutputStream(final Path path, final OpenOption... options) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        try {
            return new FileOutputStream(path.toFile());
        } catch (FileNotFoundException e) {
            throw new IOException();
        }
    }

    @Override
    public FileChannel newFileChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public AsynchronousFileChannel newAsynchronousFileChannel(final Path path, final Set<? extends OpenOption> options, final ExecutorService executor, FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options, final FileAttribute<?>... attrs) throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        final File file = checkNotNull("path", path).toFile();
        if (file.exists()) {
            throw new FileAlreadyExistsException("");
        }
        try {
            file.createNewFile();
            return new SeekableByteChannel() {
                @Override public long position() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel position(long newPosition) throws IOException {
                    return null;
                }

                @Override public long size() throws IOException {
                    return 0;
                }

                @Override public SeekableByteChannel truncate(long size) throws IOException {
                    return null;
                }

                @Override public int read(ByteBuffer dst) throws java.io.IOException {
                    return 0;
                }

                @Override public int write(ByteBuffer src) throws java.io.IOException {
                    return 0;
                }

                @Override public boolean isOpen() {
                    return false;
                }

                @Override public void close() throws java.io.IOException {
                }
            };
        } catch (java.io.IOException e) {
            throw new IOException();
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final DirectoryStream.Filter<Path> filter) throws NotDirectoryException, IOException, SecurityException {
        try {
            repository = getGuvnorNGRepository();
            final List<PathModel> files = JGitUtils.getFilesInPath(repository, dir.toString(), null);
            
            System.out.println("newDirectoryStream is invoked, Path is: " + dir.toString());
            for(PathModel p : files) {
                System.out.println(p.name);
            }
            
            return new DirectoryStream<Path>() {

                @Override
                public void close() throws IOException {
                }

                @Override
                public Iterator<Path> iterator() {
                    return new Iterator<Path>() {
                        private int i = 0;

                        @Override public boolean hasNext() {
                            return i < files.size();
                        }

                        @Override public Path next() {
                            if (i < files.size()) {
                                PathModel pathModel = files.get(i);
                                System.out.println(pathModel.path + "  isTree: " + pathModel.isTree());
                                i++;
                                return GeneralPathImpl.create(getDefaultFileSystem(), pathModel.path, false);
                                //return GeneralPathImpl.createFromFile(getDefaultFileSystem(), result);
                            } else {
                                throw new NoSuchElementException();
                            }
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        } catch (java.io.IOException e) {            
            IOException i = new IOException();
            i.initCause(e);
            throw i;
        }
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull("dir", dir).toFile().mkdirs();
    }

    @Override
    public void createSymbolicLink(final Path link, final Path target, final FileAttribute<?>... attrs) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createLink(final Path link, final Path existing) throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        checkNotNull("path", path).toFile().delete();
        //toGeneralPathImpl(path).clearCache();
    }

    @Override public boolean deleteIfExists(final Path path) throws DirectoryNotEmptyException, IOException, SecurityException {
        return checkNotNull("path", path).toFile().delete();
    }

    @Override public Path readSymbolicLink(final Path link) throws UnsupportedOperationException, NotLinkException, IOException, SecurityException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void copy(final Path source, final Path target, final CopyOption... options) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void move(Path source, Path target, CopyOption... options) throws DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public boolean isSameFile(Path path, Path path2) throws IOException, SecurityException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isHidden(final Path path) throws IllegalArgumentException, IOException, SecurityException {
        checkNotNull("path", path);
        return ((JGitlFileAttributes)getFileAttributeView(path, BasicFileAttributeView.class, null).readAttributes()).isHidden();
    }

    @Override
    public FileStore getFileStore(final Path path) throws IOException, SecurityException {
        return null;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes)
            throws UnsupportedOperationException, AccessDeniedException, IOException, SecurityException {
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        if (type == BasicFileAttributeView.class) {
            PathModel pathModel = JGitUtils.getPathModel(repository, path.toString(), null);
            return (V) new JGitFileAttributeView(pathModel);
        }

        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type, final LinkOption... options)
            throws UnsupportedOperationException, IOException, SecurityException {
        checkNotNull("path", path);
        checkNotNull("type", type);

        //TODO: Check if the path exists in git repo
/*        final GeneralPathImpl pathImpl = toGeneralPathImpl(path);
        if (!pathImpl.getAttrs().exists()) {
            throw new NoSuchFileException("");
        }*/

        if (type == BasicFileAttributes.class) {
            BasicFileAttributeView view = getFileAttributeView(path, BasicFileAttributeView.class, options);
            return (A) view.readAttributes();
        }

        return null;
    }

    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        if (attributes.equals("*")) {
            PathModel pathModel = JGitUtils.getPathModel(repository, path.toString(), null);
            JGitlFileAttributes attrs = new JGitlFileAttributes(pathModel);
            final Map<String, Object> result = new HashMap<String, Object>();
            result.put("isRegularFile", attrs.isRegularFile());
            result.put("isDirectory", attrs.isDirectory());
            result.put("isSymbolicLink", attrs.isSymbolicLink());
            result.put("isOther", attrs.isOther());
            result.put("size", new Long(attrs.size()));
            result.put("fileKey", attrs.fileKey());
            result.put("exists", attrs.exists());
            result.put("isReadable", attrs.isReadable());
            result.put("isExecutable", attrs.isExecutable());
            result.put("isHidden", attrs.isHidden());
            //todo check why errai can't serialize it
            result.put("lastModifiedTime", null);
            result.put("lastAccessTime", null);
            result.put("creationTime", null);
            return result;
        }
        throw new IOException();
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private FileSystem getDefaultFileSystem() {
        return fileSystem;
    }

    public static void main(String[] args) throws Exception {
        JGitFileSystemProvider j = new JGitFileSystemProvider();
    }
    
    public void setUpGitRepository() {
        try {
            // startGitblit();
            File gitRepoRoot = new File(REPOSITORIES_ROOT_DIR);

            if (gitRepoRoot.exists() || gitRepoRoot.mkdirs()) {
                cloneOrFetch("guvnorng.git", "git://github.com/droolsjbpm/guvnorng.git");
                showRemoteBranches("guvnorng.git");

                repository = getGuvnorNGRepository();
                
                List<PathModel> files = JGitUtils.getFilesInPath(repository, null, null);
                for (PathModel p : files) {
                    System.out.println(p.name);
                    System.out.println(p.path);
                    System.out.println("isTree: " + p.isTree());
                }

                PathModel pathModel = JGitUtils.getPathModel(repository, "guvnorng-vfs/vfs-api", null);
                
                //GeneralPathImpl p = GeneralPathImpl.create(this.fileSystem, "guvnorng-vfs/vfs-api", false);                
                //Map<String, Object> attrs = readAttributes(p, "*", null);

                String contentA = JGitUtils.getStringContent(repository, null, "guvnorng-vfs/vfs-api/pom.xml");
                System.out.println(contentA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Repository getGuvnorNGRepository() throws java.io.IOException {
        return new FileRepository(new File(REPOSITORIES_ROOT_DIR, "guvnorng.git"));
    }
    
    private static void cloneOrFetch(String name, String fromUrl) throws Exception {
        System.out.print("Fetching " + name + "... ");
        JGitUtils.cloneRepository(new File(REPOSITORIES_ROOT_DIR), name, fromUrl);
        System.out.println("done.");
    }
    
    private static void showRemoteBranches(String repositoryName) {
        try {
            FileSettings settings = new FileSettings("my.properties");
            GitBlit.self().configureContext(settings, true);
            RepositoryModel model = GitBlit.self().getRepositoryModel(repositoryName);
            model.showRemoteBranches = true;
            GitBlit.self().updateRepositoryModel(model.name, model, false);
        } catch (GitBlitException g) {
            g.printStackTrace();
        }
    }
    
}
