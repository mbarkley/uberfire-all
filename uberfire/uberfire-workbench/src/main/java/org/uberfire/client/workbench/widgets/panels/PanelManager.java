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
package org.uberfire.client.workbench.widgets.panels;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.annotations.WorkbenchPosition;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
@ApplicationScoped
public class PanelManager {

    @Inject
    @WorkbenchPosition(position = Position.NORTH)
    private PanelHelper                       helperNorth;

    @Inject
    @WorkbenchPosition(position = Position.SOUTH)
    private PanelHelper                       helperSouth;

    @Inject
    @WorkbenchPosition(position = Position.EAST)
    private PanelHelper                       helperEast;

    @Inject
    @WorkbenchPosition(position = Position.WEST)
    private PanelHelper                       helperWest;

    @Inject
    private BeanFactory                       factory;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent> workbenchPanelOnFocusEvent;

    private WorkbenchPanel                    focusPanel      = null;

    private WorkbenchPanel                    rootPanel       = null;

    private Set<WorkbenchPanel>               workbenchPanels = new HashSet<WorkbenchPanel>();

    public void setRoot(final WorkbenchPanel panel) {
        this.rootPanel = panel;
        workbenchPanels.add( panel );
        scheduleResize( panel.getPanelView() );
        setFocus( panel );
    }

    public WorkbenchPanel getRoot() {
        return this.rootPanel;
    }

    public WorkbenchPanel addWorkbenchPart(final WorkbenchPart part,
                                           final WorkbenchPanel targetPanel) {
        targetPanel.addPart( part );
        setFocus( targetPanel );
        return targetPanel;
    }

    public WorkbenchPanel addWorkbenchPanel(final Position position) {
        return addWorkbenchPanel( rootPanel,
                                  position );
    }

    public WorkbenchPanel addWorkbenchPanel(final WorkbenchPanel targetPanel,
                                            final Position position) {

        WorkbenchPanel newPanel;
        final List<PanelDefinition> panels = targetPanel.getDefinition().getChildren( position );

        switch ( position ) {
            case SELF :
                newPanel = targetPanel;
                break;

            case ROOT :
                newPanel = rootPanel;
                break;

            case NORTH :
                newPanel = factory.newWorkbenchPanel();
                panels.add( newPanel.getDefinition() );
                workbenchPanels.add( newPanel );
                helperNorth.add( newPanel.getPanelView(),
                                 targetPanel.getPanelView() );
                break;

            case SOUTH :
                newPanel = factory.newWorkbenchPanel();
                panels.add( newPanel.getDefinition() );
                workbenchPanels.add( newPanel );
                helperSouth.add( newPanel.getPanelView(),
                                 targetPanel.getPanelView() );
                break;

            case EAST :
                newPanel = factory.newWorkbenchPanel();
                panels.add( newPanel.getDefinition() );
                workbenchPanels.add( newPanel );
                helperEast.add( newPanel.getPanelView(),
                                targetPanel.getPanelView() );
                break;

            case WEST :
                newPanel = factory.newWorkbenchPanel();
                panels.add( newPanel.getDefinition() );
                workbenchPanels.add( newPanel );
                helperWest.add( newPanel.getPanelView(),
                                targetPanel.getPanelView() );
                break;

            default :
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        setFocus( newPanel );
        return newPanel;
    }

    public void removeWorkbenchPanel(final WorkbenchPanel panel) {

        //The root WorkbenchPanel cannot be removed
        if ( panel == rootPanel ) {
            return;
        }

        //Find the position that needs to be deleted
        Position position = Position.NONE;
        final WorkbenchPanel.View view = panel.getPanelView();
        final Widget parent = view.asWidget().getParent().getParent().getParent();
        if ( parent instanceof HorizontalSplitterPanel ) {
            final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) parent;
            if ( view.asWidget().equals( hsp.getWidget( Position.EAST ) ) ) {
                position = Position.EAST;
            } else if ( view.asWidget().equals( hsp.getWidget( Position.WEST ) ) ) {
                position = Position.WEST;
            }
        } else if ( parent instanceof VerticalSplitterPanel ) {
            final VerticalSplitterPanel vsp = (VerticalSplitterPanel) parent;
            if ( view.asWidget().equals( vsp.getWidget( Position.NORTH ) ) ) {
                position = Position.NORTH;
            } else if ( view.asWidget().equals( vsp.getWidget( Position.SOUTH ) ) ) {
                position = Position.SOUTH;
            }
        }

        switch ( position ) {
            case NORTH :
                helperNorth.remove( view );
                workbenchPanels.remove( panel );
                factory.destroy( panel );
                break;

            case SOUTH :
                helperSouth.remove( view );
                workbenchPanels.remove( panel );
                factory.destroy( panel );
                break;

            case EAST :
                helperEast.remove( view );
                workbenchPanels.remove( panel );
                factory.destroy( panel );
                break;

            case WEST :
                helperWest.remove( view );
                workbenchPanels.remove( panel );
                factory.destroy( panel );
                break;
        }

        if ( this.focusPanel == panel ) {
            this.focusPanel = null;
            assertFocusPanel();
            setFocus( this.focusPanel );
        }
    }

    private void setFocus(final WorkbenchPanel panel) {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( panel ) );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final WorkbenchPanel panel = event.getWorkbenchPanel();
        this.focusPanel = panel;
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent(@Observes WorkbenchPartCloseEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        removeWorkbenchPart( part );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent(@Observes WorkbenchPartDroppedEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        removeWorkbenchPart( part );
    }

    private void assertFocusPanel() {
        if ( this.focusPanel == null ) {
            this.focusPanel = rootPanel;
        }
    }

    private void removeWorkbenchPart(WorkbenchPart workbenchPart) {
        for ( WorkbenchPanel workbenchPanel : workbenchPanels ) {
            if ( workbenchPanel.contains( workbenchPart ) ) {
                workbenchPanel.remove( workbenchPart );
                factory.destroy( workbenchPart );
                return;
            }
        }
    }

    private void scheduleResize(final RequiresResize widget) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

}
