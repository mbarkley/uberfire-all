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
package org.drools.guvnor.client.workbench.widgets.panels;

import org.drools.guvnor.client.workbench.Position;
import org.drools.guvnor.client.workbench.WorkbenchPanel;
import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class VerticalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    private final WorkbenchSplitLayoutPanel slp                  = new WorkbenchSplitLayoutPanel();
    private final SimpleLayoutPanel         northWidgetContainer = new SimpleLayoutPanel();
    private final SimpleLayoutPanel         southWidgetContainer = new SimpleLayoutPanel();

    public VerticalSplitterPanel(final WorkbenchPanel northWidget,
                                 final WorkbenchPanel southWidget,
                                 final Position position) {
        switch ( position ) {
            case NORTH :
                slp.addNorth( northWidgetContainer,
                              INITIAL_SIZE );
                slp.add( southWidgetContainer );
                break;
            case SOUTH :
                slp.addSouth( southWidgetContainer,
                              INITIAL_SIZE );
                slp.add( northWidgetContainer );
                break;
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
        slp.setWidgetMinSize( northWidgetContainer,
                              MIN_SIZE );
        slp.setWidgetMinSize( southWidgetContainer,
                              MIN_SIZE );

        northWidgetContainer.setWidget( northWidget );
        southWidgetContainer.setWidget( southWidget );

        initWidget( slp );

        //Wire-up DnD controllers
        WorkbenchDragAndDropManager.getInstance().registerDropController( northWidgetContainer,
                                                                          new CompassDropController( northWidget ) );
        WorkbenchDragAndDropManager.getInstance().registerDropController( southWidgetContainer,
                                                                          new CompassDropController( southWidget ) );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget(Position position) {
        switch ( position ) {
            case NORTH :
                return this.northWidgetContainer.getWidget();
            case SOUTH :
                return this.southWidgetContainer.getWidget();
            default :
                throw new IllegalArgumentException( "position must be either NORTH or SOUTH" );
        }
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        setPixelSize( parent.getOffsetWidth(),
                      parent.getOffsetHeight() );
        super.onResize();
    }

}
