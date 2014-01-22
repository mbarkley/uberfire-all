package org.uberfire.backend.server.plugin;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.jboss.errai.bus.server.annotations.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.uberfire.backend.plugin.RuntimePluginsService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

@Service
@ApplicationScoped
public class RuntimePluginsServiceServerImpl implements RuntimePluginsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RuntimePluginsServiceServerImpl.class);

    @Inject
    @Named("uf")
    private ServletContext servletContext;

    @Override
    public Collection<String> listFramworksContent() {
        return directoryContent( "frameworks", "*.js" );
    }

    @Override
    public Collection<String> listPluginsContent() {
        return directoryContent( "plugins", "*.js" );
    }

    @Override
    public String getTemplateContent( String url ) {
      String realPath = getRealPath( "plugins" );
      if (realPath == null) {
        LOGGER.info("Not fetching template content for " + url + " because getRealPath() is"
                + " returning null. (This app is probably deployed in an unexploded .war)");
        return "";
      }
        final Path template;
        if ( url.startsWith( "/" ) ) {
            template = Paths.get( URI.create( "file://" + realPath + url ) );
        } else {
            template = Paths.get( URI.create( "file://" + realPath + "/" + url ) );
        }

        if ( Files.isRegularFile( template ) ) {
            return new String( Files.readAllBytes( template ) );
        }
        return "";
    }

    private Collection<String> directoryContent( final String directory,
                                                 final String glob ) {
      String realPath = getRealPath( directory );
      if (realPath == null) {
        LOGGER.info("Not listing directory content for " + directory + "/" + glob +
                " because getRealPath() is returning null. (This app is probably deployed in an unexploded .war)");
        return Collections.emptyList();
      }
        final Collection<String> result = new ArrayList<String>();

        final Path pluginsRootPath = Paths.get( URI.create( "file://" + realPath ) );

        if ( Files.isDirectory( pluginsRootPath ) ) {
            final DirectoryStream<Path> stream = Files.newDirectoryStream( pluginsRootPath, glob );

            for ( final Path activeJS : stream ) {
                result.add( new String( Files.readAllBytes( activeJS ) ) );
            }
        }

        return result;
    }

    private String getRealPath( final String path ) {
        String realPath = servletContext.getRealPath( path );
        if (realPath == null) {
          return null;
        }
        else {
          return realPath.replaceAll( "\\\\", "/" ).replaceAll( " ", "%20" );
        }
    }

}
