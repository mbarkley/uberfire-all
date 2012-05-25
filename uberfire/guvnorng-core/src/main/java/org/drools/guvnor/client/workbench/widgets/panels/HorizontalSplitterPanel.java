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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 */
public class HorizontalSplitterPanel extends ResizeComposite
    implements
    SplitPanel {

    private final WorkbenchSplitLayoutPanel slp                 = new WorkbenchSplitLayoutPanel();
    private final ScrollPanel               eastWidgetContainer = new ScrollPanel();
    private final ScrollPanel               westWidgetContainer = new ScrollPanel();

    public HorizontalSplitterPanel(final WorkbenchPanel eastWidget,
                                   final WorkbenchPanel westWidget,
                                   final Position position) {
        switch ( position ) {
            case EAST :
                slp.addEast( westWidgetContainer,
                             INITIAL_SIZE );
                slp.add( eastWidgetContainer );
                break;
            case WEST :
                slp.addWest( eastWidgetContainer,
                             INITIAL_SIZE );
                slp.add( westWidgetContainer );
                break;
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
        slp.setWidgetMinSize( eastWidgetContainer,
                              MIN_SIZE );
        slp.setWidgetMinSize( westWidgetContainer,
                              MIN_SIZE );

        westWidgetContainer.setWidget( westWidget );
        eastWidgetContainer.setWidget( eastWidget );

        initWidget( slp );

        //Wire-up DnD controllers
        WorkbenchDragAndDropManager.getInstance().registerDropController( eastWidgetContainer,
                                                                          new CompassDropController( eastWidget ) );
        WorkbenchDragAndDropManager.getInstance().registerDropController( westWidgetContainer,
                                                                          new CompassDropController( westWidget ) );
    }

    @Override
    public void clear() {
        this.slp.clear();
    }

    @Override
    public Widget getWidget(Position position) {
        switch ( position ) {
            case EAST :
                return this.westWidgetContainer.getWidget();
            case WEST :
                return this.eastWidgetContainer.getWidget();
            default :
                throw new IllegalArgumentException( "position must be either EAST or WEST" );
        }
    }

    @Override
    public void onResize() {
        Widget parent = getParent();
        int width = parent.getOffsetWidth();
        int height = parent.getOffsetHeight();
        this.setPixelSize( width,
                           height );
        super.onResize();
    }

}
