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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.client.workbench.widgets.panels.WorkbenchTabLayoutPanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
public class WorkbenchPanel extends ResizeComposite {

    public static final int                      TAB_BAR_HEIGHT   = 32;

    private static final int                     FOCUS_BAR_HEIGHT = 3;

    @Inject
    private PanelManager                         panelManager;

    @Inject
    private WorkbenchDragAndDropManager          dndManager;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<WorkbenchPartOnFocusEvent>     workbenchPartOnFocusEvent;

    @Inject
    private Event<WorkbenchPartLostFocusEvent>   workbenchPartLostFocusEvent;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;

    private final WorkbenchTabLayoutPanel        tabPanel;

    public WorkbenchPanel() {
        this.tabPanel = makeTabPanel();
        initWidget( this.tabPanel );
    }

    public void addTab(final WorkbenchPart part) {
        tabPanel.add( part,
                      makeTabWidget( part ) );
        tabPanel.selectTab( part );
    }

    public void clear() {
        tabPanel.clear();
    }

    private WorkbenchTabLayoutPanel makeTabPanel() {
        final WorkbenchTabLayoutPanel tabPanel = new WorkbenchTabLayoutPanel( TAB_BAR_HEIGHT,
                                                                              FOCUS_BAR_HEIGHT,
                                                                              Unit.PX );

        //Selecting a tab causes the previously selected tab to receive a Lost Focus event
        tabPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {

            @Override
            public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
                int previousTabIndex = tabPanel.getSelectedIndex();
                if ( previousTabIndex < 0 ) {
                    return;
                }
                final Widget w = tabPanel.getWidget( previousTabIndex );
                if ( w instanceof WorkbenchPart ) {
                    workbenchPartLostFocusEvent.fire( new WorkbenchPartLostFocusEvent( (WorkbenchPart) w ) );
                }
            }
        } );

        //When a tab is selected ensure content is resized and set focus
        tabPanel.addSelectionHandler( new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                final Widget w = tabPanel.getWidget( event.getSelectedItem() );
                if ( w instanceof RequiresResize ) {
                    scheduleResize( (RequiresResize) w );
                }
                if ( w instanceof WorkbenchPart ) {
                    workbenchPartOnFocusEvent.fire( new WorkbenchPartOnFocusEvent( (WorkbenchPart) w ) );
                }
            }

        } );
        return tabPanel;
    }

    private Widget makeTabWidget(final WorkbenchPart part) {
        final FlowPanel fp = new FlowPanel();
        final InlineLabel tabLabel = new InlineLabel( part.getPartTitle() );
        fp.add( tabLabel );

        //Clicking on the Tab takes focus
        fp.addDomHandler( new ClickHandler() {

                              @Override
                              public void onClick(ClickEvent event) {
                                  workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( WorkbenchPanel.this ) );
                              }

                          },
                          ClickEvent.getType() );

        dndManager.makeDraggable( part,
                                  tabLabel );

        final FocusPanel image = new FocusPanel();
        image.getElement().getStyle().setFloat( Style.Float.RIGHT );

        image.setStyleName( WorkbenchResources.INSTANCE.CSS().closeTabImage() );
        image.addClickHandler( new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                workbenchPartBeforeCloseEvent.fire( new WorkbenchPartBeforeCloseEvent( part ) );
            }

        } );
        fp.add( image );
        return fp;
    }

    private void scheduleResize(final RequiresResize widget) {
        Scheduler.get().scheduleDeferred( new ScheduledCommand() {

            @Override
            public void execute() {
                widget.onResize();
            }

        } );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final WorkbenchPanel panel = event.getWorkbenchPanel();
        this.tabPanel.setFocus( panel == this );
    }

    @SuppressWarnings("unused")
    private void onSelectWorkbenchPartEvent(@Observes SelectWorkbenchPartEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        if ( contains( part ) ) {
            tabPanel.selectTab( part );
            workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( WorkbenchPanel.this ) );
        }
    }

    public boolean contains(WorkbenchPart workbenchPart) {
        return tabPanel.getWidgetIndex( workbenchPart ) >= 0;
    }

    public boolean remove(WorkbenchPart part) {
        final int indexOfTabToRemove = tabPanel.getWidgetIndex( part );
        final int indexOfSelectedTab = tabPanel.getSelectedIndex();
        final boolean removed = tabPanel.remove( part );

        if ( removed ) {

            if ( tabPanel.getWidgetCount() == 0 ) {
                panelManager.removeWorkbenchPanel( this );
            } else {
                if ( indexOfSelectedTab == indexOfTabToRemove ) {
                    tabPanel.selectTab( indexOfTabToRemove > 0 ? indexOfTabToRemove - 1 : 0 );
                }
            }
        }
        return removed;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        setPixelSize( parent.getOffsetWidth(),
                      parent.getOffsetHeight() );
        super.onResize();
    }

}
