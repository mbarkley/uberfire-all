/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.Paths;
import org.uberfire.client.editors.texteditor.TextEditorPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.ShowcaseResources;
import org.uberfire.client.workbench.widgets.menu.Command;
import org.uberfire.client.workbench.widgets.menu.CommandMenuItem;
import org.uberfire.client.workbench.widgets.menu.SubMenuItem;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private IOCBeanManager            manager;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private PlaceManager              placeManager;

    private String[]                  menuItems = new String[]{"MyAdminArea", "MyAdminArea2", "Monitoring", "Test", "Test2", "FileExplorer", "RepositoriesEditor"};

    @AfterInitialization
    public void startApp() {
        loadStyles();
        setupMenu();

        if ( Window.Location.getPath().contains( "Standalone.html" ) ) {
            //TODO THIS SHOULD BE MOVED TO CORE SOON - LOOKUP SHOULD BE BASED ON CODE GEN!
            final TextEditorPresenter presenter = manager.lookupBean( TextEditorPresenter.class ).getInstance();
            RootLayoutPanel.get().add( presenter.view );

            Path path = null;
            final String pathURI = Window.Location.getParameter( "path" );
            if ( pathURI != null ) {
                path = Paths.fromURI( pathURI );
            }

            presenter.onStart( path );
            presenter.onReveal();
        }
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        ShowcaseResources.INSTANCE.CSS().ensureInjected();
    }

    private void setupMenu() {
        //Places sub-menu
        final WorkbenchMenuBar placesMenuBar = new WorkbenchMenuBar();
        final SubMenuItem placesMenu = new SubMenuItem( "Places",
                                                        placesMenuBar );

        //Add places
        Arrays.sort( menuItems );
        for ( final String menuItem : menuItems ) {
            final CommandMenuItem item = new CommandMenuItem( menuItem,
                                                              new Command() {

                                                                  @Override
                                                                  public void execute() {
                                                                      placeManager.goTo( new PlaceRequest( menuItem ) );
                                                                  }

                                                              } );
            placesMenuBar.addItem( item );
        }
        menubar.addMenuItem( placesMenu );
    }

}
