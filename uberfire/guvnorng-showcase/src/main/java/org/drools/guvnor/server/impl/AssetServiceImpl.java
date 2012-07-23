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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.backend.vfs.Path;
import org.drools.guvnor.server.contenthandler.drools.FactModelContentHandler;
import org.drools.guvnor.shared.AssetService;
import org.drools.guvnor.shared.common.vo.asset.AbstractAsset;
import org.drools.guvnor.shared.common.vo.assets.enums.EnumModel;
import org.drools.guvnor.shared.common.vo.assets.factmodel.FactModels;
import org.drools.java.nio.file.Files;
import org.drools.java.nio.file.Paths;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class AssetServiceImpl
    implements
    AssetService {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /*
     * @Override public <V> V loadAsset(Path path, Class<V> type) { if (type ==
     * FactModels.class) { FactModels asset = new FactModels(); return (V)asset;
     * } return null; }
     */

    @Override
    public AbstractAsset loadAsset(final Path path,
                                   final String type) {
        try {
            final List<String> lines = Files.readAllLines( fromPath(path), UTF_8 );
            final StringBuilder sb = new StringBuilder();
            if (lines != null ){
                for (final String s : lines) {
                    sb.append(s).append('\n');
                }
            }

            if ( type.equals( "model.drl" ) ) {
                FactModels asset = new FactModels();
                FactModelContentHandler handler = new FactModelContentHandler();
                handler.retrieveAssetContent( asset,
                                              sb.toString() );
                return asset;
            }
            if ( type.equals( "enumeration" ) ) {
                EnumModel asset = new EnumModel();
                asset.setDefinitions( sb.toString() );
                return asset;
            }
        } catch ( Exception e ) {

        }
        return null;
    }

    /*
     * //@Override public List<JGitRepositoryConfigurationVO> listRepositories()
     * { try { List<FileSystem> fileSystems = vfsService.listJGitFileSystems();
     * List<JGitRepositoryConfigurationVO> repositories = new
     * ArrayList<JGitRepositoryConfigurationVO>(); for (FileSystem f :
     * fileSystems) { //TODO: how to get info following info
     * JGitRepositoryConfigurationVO jGitRepositoryConfiguration = new
     * JGitRepositoryConfigurationVO();
     * jGitRepositoryConfiguration.setFromGitURL
     * ("https://github.com/guvnorngtestuser1/guvnorng-playground.git");
     * jGitRepositoryConfiguration.setRepositoryName("guvnorng-playground"); URI
     * uri = URI.create("jgit:///" + "guvnorng-playground");
     * jGitRepositoryConfiguration.setRootURI(uri);
     * repositories.add(jGitRepositoryConfiguration); } return repositories; }
     * catch (Exception e) { } return null; }
     */

    private org.drools.java.nio.file.Path fromPath(final Path path) {
        //HACK: REVISIT: how to encode. We dont want to encode the whole URI string, we only want to encode the path element
        String pathString = path.toURI();
        pathString = pathString.replaceAll(" ", "%20");
        return Paths.get(URI.create(pathString));
    }

}
