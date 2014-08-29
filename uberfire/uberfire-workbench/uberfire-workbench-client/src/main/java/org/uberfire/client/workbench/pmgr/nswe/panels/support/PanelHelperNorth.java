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
package org.uberfire.client.workbench.pmgr.nswe.panels.support;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.pmgr.nswe.panels.impl.VerticalSplitterPanel;
import org.uberfire.workbench.model.CompassPosition;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to add or remove WorkbenchPanels from the North of a
 * VerticalSplitterPanel.
 */
public class PanelHelperNorth extends AbstractPanelHelper {

    protected PanelHelperNorth( BeanFactory factory ) {
        super( factory );
    }

    @Override
    public void remove( final WorkbenchPanelView panel ) {
        final VerticalSplitterPanel vsp = (VerticalSplitterPanel) panel.asWidget().getParent().getParent().getParent();
        final Widget parent = vsp.getParent();
        final Widget southWidget = vsp.getWidget( CompassPosition.SOUTH );

        vsp.clear();

        //Set parent's content to the SOUTH widget
        if ( parent instanceof SimplePanel ) {
            ( (SimplePanel) parent ).setWidget( southWidget );
        }

        if ( southWidget instanceof RequiresResize ) {
            scheduleResize( (RequiresResize) southWidget );
        }
    }

    private void scheduleResize( final RequiresResize widget ) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

}
