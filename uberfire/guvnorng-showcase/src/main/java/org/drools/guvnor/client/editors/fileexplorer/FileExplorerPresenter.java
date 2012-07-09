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

package org.drools.guvnor.client.editors.fileexplorer;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.DefaultPosition;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.annotations.WorkbenchScreen;
import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.IdentifierUtils;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.PlaceRequest;
import org.drools.guvnor.client.resources.ShowcaseImages;
import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.shared.AssetService;
import org.drools.guvnor.vfs.JGitRepositoryConfigurationVO;
import org.drools.guvnor.vfs.Path;
import org.drools.guvnor.vfs.VFSService;
import org.drools.guvnor.vfs.VFSTempUtil;
import org.drools.guvnor.vfs.impl.PathImpl;
import org.drools.java.nio.file.DirectoryStream;
import org.drools.java.nio.file.attribute.BasicFileAttributes;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter {

    @Inject
    private View                 view;

    @Inject
    private Caller<VFSService>   vfsService;

    @Inject
    private Caller<AssetService> assetService;

    @Inject
    private PlaceManager         placeManager;

    @Inject
    private IdentifierUtils      idUtils;

    private static final String  REPOSITORY_ID = "repositories";

    public interface View
        extends
        IsWidget {

        TreeItem getRootItem();

        Tree getTree();

        void setFocus();
    }

    private static ShowcaseImages images    = GWT.create( ShowcaseImages.class );
    private static final String   LAZY_LOAD = "Loading...";

    @OnStart
    public void onStart() {
        view.getRootItem().setUserObject( REPOSITORY_ID );
        view.getRootItem().addItem( LAZY_LOAD );
        vfsService.call( new RemoteCallback<List<JGitRepositoryConfigurationVO>>() {
            @Override
            public void callback(List<JGitRepositoryConfigurationVO> repositories) {

                PlaceRequest placeRequest = new PlaceRequest( "RepositoriesEditor" );
                placeManager.goTo( placeRequest );

                view.getRootItem().removeItems();
                for ( final JGitRepositoryConfigurationVO r : repositories ) {
                    final TreeItem repositoryRootItem = view.getRootItem().addItem( Util.getHeader( images.packageIcon(),
                                                                                                    r.getRepositoryName() ) );
                    repositoryRootItem.setState( true );
                    //repositoryRootItem.addItem( LAZY_LOAD );
                    repositoryRootItem.setUserObject( r );

                    PathImpl p = new PathImpl( r.getRootURI().toString() );
                    vfsService.call( new RemoteCallback<DirectoryStream<Path>>() {
                        @Override
                        public void callback(DirectoryStream<Path> response) {
                            for ( final Path path : response ) {
                                vfsService.call( new RemoteCallback<Map>() {
                                    @Override
                                    public void callback(final Map response) {
                                        final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                                        final TreeItem item;
                                        if ( attrs.isDirectory() ) {
                                            item = repositoryRootItem.addItem( Util.getHeader( images.openedFolder(),
                                                                                               path.getFileName() ) );
                                            item.addItem( LAZY_LOAD );
                                        } else {
                                            item = repositoryRootItem.addItem( Util.getHeader( images.file(),
                                                                                               path.getFileName() ) );
                                        }
                                        item.setUserObject( path );
                                    }
                                } ).readAttributes( path );
                            }
                        }
                    } ).newDirectoryStream( p );
                }
            }
        } ).listJGitRepositories();

        view.getTree().addOpenHandler( new OpenHandler<TreeItem>() {
            @Override
            public void onOpen(final OpenEvent<TreeItem> event) {
                if ( needsLoading( event.getTarget() ) && event.getTarget().getUserObject() instanceof Path ) {
                    vfsService.call( new RemoteCallback<DirectoryStream<Path>>() {
                        @Override
                        public void callback(DirectoryStream<Path> response) {
                            event.getTarget().getChild( 0 ).remove();
                            for ( final Path path : response ) {
                                vfsService.call( new RemoteCallback<Map>() {
                                    @Override
                                    public void callback(final Map response) {
                                        final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                                        final TreeItem item;
                                        if ( attrs.isDirectory() ) {
                                            item = event.getTarget().addItem( Util.getHeader( images.openedFolder(),
                                                                                              path.getFileName() ) );
                                            item.addItem( LAZY_LOAD );
                                        } else {
                                            item = event.getTarget().addItem( Util.getHeader( images.file(),
                                                                                              path.getFileName() ) );
                                        }
                                        item.setUserObject( path );
                                    }
                                } ).readAttributes( path );
                            }
                        }
                    } ).newDirectoryStream( (Path) event.getTarget().getUserObject() );
                }
            }
        } );

        view.getTree().addSelectionHandler( new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                if ( event.getSelectedItem().getUserObject() instanceof Path ) {
                    final Path path = (Path) event.getSelectedItem().getUserObject();
                    vfsService.call( new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            final BasicFileAttributes attrs = VFSTempUtil.toBasicFileAttributes( response );
                            if ( attrs.isRegularFile() ) {
                                IPlaceRequest placeRequest = getPlace( path );
                                placeManager.goTo( placeRequest );
                            }
                        }
                    } ).readAttributes( path );
                } else if ( event.getSelectedItem().getUserObject() instanceof String && ((String) event.getSelectedItem().getUserObject()).equals( REPOSITORY_ID ) ) {
                    PlaceRequest placeRequest = new PlaceRequest( "RepositoriesEditor" );
                    placeManager.goTo( placeRequest );
                } else if ( event.getSelectedItem().getUserObject() instanceof JGitRepositoryConfigurationVO ) {
                    final JGitRepositoryConfigurationVO jGitRepositoryConfigurationVO = (JGitRepositoryConfigurationVO) event.getSelectedItem().getUserObject();
                    PlaceRequest placeRequest = new PlaceRequest( "RepositoryEditor" );
                    placeRequest.addParameter( "description",
                                               jGitRepositoryConfigurationVO.getDescription() );
                    placeRequest.addParameter( "gitURL",
                                               jGitRepositoryConfigurationVO.getGitURL() );
                    placeRequest.addParameter( "repositoryName",
                                               jGitRepositoryConfigurationVO.getRepositoryName() );
                    placeManager.goTo( placeRequest );
                }
            }
        } );
    }

    private IPlaceRequest getPlace(final Path path) {

        final String fileType = getFileType( path.getFileName() );
        if ( fileType == null ) {
            return defaultPlace( path );
        }

        //Lookup an Activity that can handle the file extension and create a corresponding PlaceRequest.
        //We could simply construct a PlaceRequest for the fileType and leave PlaceManager to determine whether
        //an Activity for the fileType exists however that would place the decision as to what default editor
        //to use within PlaceManager. It is a design decision to let FileExplorer determine the default editor.
        //Consequentially we check for an Activity here and, if none found, define the default editor.
        final Set<Activity> activities = idUtils.getActivities( fileType );
        if ( activities.size() > 0 ) {
            final IPlaceRequest place = new PlaceRequest( fileType );
            place.addParameter( "path",
                                path.toURI() );
            return place;
        }

        //If a specific handler was not found use a TextEditor
        return defaultPlace( path );
    }

    private PlaceRequest defaultPlace(final Path path) {
        PlaceRequest defaultPlace = new PlaceRequest();
        defaultPlace.addParameter( "path",
                                   path.toURI() );
        return defaultPlace;
    }

    private String getFileType(final String fileName) {
        final int dotIndex = fileName.indexOf( "." );
        if ( dotIndex >= 0 ) {
            return fileName.substring( dotIndex + 1 );
        }
        return null;
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "File Explorer";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.WEST;
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals( item.getChild( 0 ).getText() );
    }
}
