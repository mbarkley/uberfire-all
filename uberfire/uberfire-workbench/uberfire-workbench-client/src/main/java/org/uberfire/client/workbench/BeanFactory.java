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
package org.uberfire.client.workbench;

import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.HorizontalSplitterPanel;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.VerticalSplitterPanel;
import org.uberfire.client.workbench.widgets.dnd.CompassDropController;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.menu.Menus;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A Factory definition to create new instances of managed beans.
 */
public interface BeanFactory {

    public WorkbenchPartPresenter newWorkbenchPart( final Menus menus,
                                                    final String title,
                                                    final IsWidget titleDecoration,
                                                    final PartDefinition definition );

    /**
     * Creates a new perspective root panel for the given Perspective Activity and Root Panel Definition. The returned
     * object must be destroyed by a call to {@link #destroy(Object)} when it is no longer needed.
     * 
     * @param activity
     *            the perspective that the root panel is being created for. Must not be null.
     * @param root
     *            description of the panel to create. Must not be null.
     * @return a new WorkbenchPanelPresenter configured as specified in the given panel definition.
     */
    public WorkbenchPanelPresenter newRootPanel( PerspectiveActivity activity,
                                                 PanelDefinition root );

    /**
     * Creates a new panel
     * @param definition
     * @return
     */
    public WorkbenchPanelPresenter newWorkbenchPanel( final PanelDefinition definition );

    public HorizontalSplitterPanel newHorizontalSplitterPanel( final WorkbenchPanelView eastPanel,
                                                               final WorkbenchPanelView westPanel,
                                                               final CompassPosition position,
                                                               final Integer preferredSize,
                                                               final Integer preferredMinSize );

    public VerticalSplitterPanel newVerticalSplitterPanel( final WorkbenchPanelView northPanel,
                                                           final WorkbenchPanelView southPanel,
                                                           final CompassPosition position,
                                                           final Integer preferredSize,
                                                           final Integer preferredMinSize );

    public CompassDropController newDropController( final WorkbenchPanelView view );

    /**
     * Destroys the entire graph of beans that were created and returned via a call to any of the <tt>newXXX()</tt>
     * methods in this class. For example, passing a {@link WorkbenchPartPresenter} instance in will result in the
     * destruction of that presenter, its view, and all other dependent beans injected into that graph of objects.
     * 
     * @param o
     *            a bean which was returned from one of the <tt>newXXX()</tt> methods in this class and which has not
     *            been destroyed yet.
     */
    public void destroy( final Object o );

}
