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
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.resources.ShowcaseResources;
import org.uberfire.client.workbench.widgets.menu.CommandMenuItem;
import org.uberfire.client.workbench.widgets.menu.SubMenuItem;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.shared.mvp.impl.PlaceRequestImpl;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * GWT's Entry-point for Uberfire-showcase
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private IOCBeanManager                  manager;

    @Inject
    private WorkbenchMenuBarPresenter       menubar;

    @Inject
    private Caller<FileExplorerRootService> rootService;

    @Inject
    private PlaceManager                    placeManager;

    private String[]                        menuItems = new String[]{"MyAdminArea", "MyAdminArea2", "Monitoring", "Test", "Test2", "FileExplorer", "RepositoriesEditor", "Chart", "chartPopulator", "GoogleGadgetScreen"};

    @AfterInitialization
    public void startApp() {
        loadStyles();
        setupMenu();
        hideLoadingPopup();

        //Register call-backs for demo
        placeManager.registerCallback( new PlaceRequestImpl( "TestPerspective" ),
                                       new Command() {

                                           @Override
                                           public void execute() {
                                               Window.alert( "Callback!" );
                                           }

                                       } );
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
                                                                      placeManager.goTo( new PlaceRequestImpl( menuItem ) );
                                                                  }

                                                              } );
            placesMenuBar.addItem( item );
        }

        //Add places
        final CommandMenuItem item = new CommandMenuItem( "Logout",
                                                          new Command() {
                                                              @Override
                                                              public void execute() {
                                                                  redirect( "/uf_logout" );
                                                              }
                                                          } );
        placesMenuBar.addItem( item );

        menubar.addMenuItem( placesMenu );
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    public static native void redirect(String url)/*-{
        $wnd.location = url;
    }-*/;

}
