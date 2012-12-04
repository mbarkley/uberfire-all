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

package org.uberfire.backend.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.AtomicMoveNotSupportedException;
import org.kie.commons.java.nio.file.CopyOption;
import org.kie.commons.java.nio.file.DirectoryNotEmptyException;
import org.kie.commons.java.nio.file.DirectoryStream;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.FileSystemAlreadyExistsException;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.commons.java.nio.file.NotDirectoryException;
import org.kie.commons.java.nio.file.OpenOption;
import org.kie.commons.java.nio.file.ProviderNotFoundException;
import org.uberfire.backend.vfs.FileSystem;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.DirectoryStreamImpl;
import org.uberfire.backend.vfs.impl.FileSystemImpl;
import org.uberfire.backend.vfs.impl.PathImpl;

@Service
@ApplicationScoped
public class VFSServicesServerImpl implements VFSService {

    @Inject @Named("ioStrategy")
    private IOService ioService;

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir )
            throws IllegalArgumentException, NotDirectoryException, IOException {

        final Iterator<org.kie.commons.java.nio.file.Path> content = ioService.newDirectoryStream( convert( dir ) ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream( final Path dir,
                                                     final DirectoryStream.Filter<Path> filter )
            throws IllegalArgumentException, NotDirectoryException, IOException {
        final Iterator<org.kie.commons.java.nio.file.Path> content = ioService.newDirectoryStream( convert( dir ), convert( filter ) ).iterator();

        return newDirectoryStream( content );
    }

    @Override
    public Path createDirectory( final Path dir )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        return convert( ioService.createDirectory( convert( dir ) ) );
    }

    @Override
    public Path createDirectories( final Path dir )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return convert( ioService.createDirectories( convert( dir ) ) );
    }

    @Override
    public Path createDirectory( final Path dir,
                                 final Map<String, ?> attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return convert( ioService.createDirectory( convert( dir ), attrs ) );
    }

    @Override
    public Path createDirectories( final Path dir,
                                   final Map<String, ?> attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException, IOException, SecurityException {
        return convert( ioService.createDirectories( convert( dir ), attrs ) );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path ) throws UnsupportedOperationException, IllegalArgumentException, IOException {
        return ioService.readAttributes( convert( path ) );
    }

    @Override
    public void setAttributes( final Path path,
                               final Map<String, Object> attrs ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        ioService.setAttributes( convert( path ), attrs );
    }

    @Override
    public void delete( final Path path ) throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException, IOException {
        ioService.delete( convert( path ) );
    }

    @Override
    public boolean deleteIfExists( final Path path ) throws IllegalArgumentException, DirectoryNotEmptyException, IOException {
        return ioService.deleteIfExists( convert( path ) );
    }

    @Override
    public Path copy( final Path source,
                      final Path target,
                      final CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, IOException {
        return convert( ioService.copy( convert( source ), convert( target ), options ) );
    }

    @Override
    public Path move( final Path source,
                      final Path target,
                      final CopyOption... options ) throws UnsupportedOperationException, FileAlreadyExistsException, DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException {
        return convert( ioService.move( convert( source ), convert( target ), options ) );
    }

    @Override
    public String readAllString( final Path path ) throws IllegalArgumentException, NoSuchFileException, IOException {
        return ioService.readAllString( convert( path ) );
    }

    @Override
    public Path write( final Path path,
                       final String content ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert( ioService.write( convert( path ), content ) );
    }

    @Override
    public Path write( final Path path,
                       final String content,
                       final Map<String, ?> attrs,
                       final OpenOption... options ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        return convert( ioService.write( convert( path ), content, attrs, options ) );
    }

    @Override
    public FileSystem newFileSystem( final String uri,
                                     final Map<String, Object> env ) throws IllegalArgumentException, FileSystemAlreadyExistsException, ProviderNotFoundException {
        final URI furi = URI.create( uri );
        return convert( ioService.newFileSystem( furi, env ) );
    }

    private DirectoryStream<Path> newDirectoryStream( final Iterator<org.kie.commons.java.nio.file.Path> iterator ) {
        final List<Path> content = new LinkedList<Path>();
        while ( iterator.hasNext() ) {
            content.add( convert( iterator.next() ) );
        }
        return new DirectoryStreamImpl( content );
    }

    private FileSystem convert( final org.kie.commons.java.nio.file.FileSystem fs ) {
        final List<Path> roots = new ArrayList<Path>();
        for ( final org.kie.commons.java.nio.file.Path root : fs.getRootDirectories() ) {
            roots.add( convert( root ) );
        }

        return new FileSystemImpl( roots );
    }

    private DirectoryStream.Filter<org.kie.commons.java.nio.file.Path> convert( final DirectoryStream.Filter<Path> filter ) {
        return new DirectoryStream.Filter<org.kie.commons.java.nio.file.Path>() {
            @Override
            public boolean accept( final org.kie.commons.java.nio.file.Path entry ) throws IOException {
                return filter.accept( convert( entry ) );
            }
        };
    }

    private Path convert( final org.kie.commons.java.nio.file.Path path ) {
        final Map<String, Object> attributes = ioService.readAttributes( path, "*" );

        return new PathImpl( path.getFileName().toString(), path.toUri().toString(), attributes );
    }

    private org.kie.commons.java.nio.file.Path convert( final Path path ) {
        try {
            return ioService.get( URI.create( path.toURI() ) );
        } catch ( IllegalArgumentException e ) {
            try {
                return ioService.get( URI.create( URIUtil.encodePath( path.toURI() ) ) );
            } catch ( URIException ex ) {
                return null;
            }
        }
    }
}
