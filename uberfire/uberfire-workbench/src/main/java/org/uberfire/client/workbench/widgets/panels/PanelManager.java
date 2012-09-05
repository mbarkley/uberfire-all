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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPanel;
import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPanelOnFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartDroppedEvent;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Manager responsible for adding or removing WorkbenchParts to WorkbenchPanels;
 * either as a consequence of explicitly opening or closing WorkbenchParts or
 * implicitly as part of a drag operation.
 */
@ApplicationScoped
public class PanelManager {

    @Inject
    private BeanFactory                          factory;

    @Inject
    private Event<WorkbenchPanelOnFocusEvent>    workbenchPanelOnFocusEvent;

    private PanelDefinition                      root                          = null;

    private Map<PartDefinition, WorkbenchPart>   mapPartDefinitionToPresenter  = new HashMap<PartDefinition, WorkbenchPart>();

    private Map<PanelDefinition, WorkbenchPanel> mapPanelDefinitionToPresenter = new HashMap<PanelDefinition, WorkbenchPanel>();

    public PanelDefinition getRoot() {
        return this.root;
    }

    public void setRoot(final PanelDefinition panel) {
        if ( !panel.isRoot() ) {
            throw new IllegalArgumentException( "Panel is not a root panel." );
        }

        if ( panel.isRoot() ) {
            if ( root == null ) {
                this.root = panel;
            } else {
                throw new IllegalArgumentException( "Root has already been set. Unable to set root." );
            }
        }

        WorkbenchPanel panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            panelPresenter = factory.newWorkbenchPanel( panel );
            mapPanelDefinitionToPresenter.put( panel,
                                               panelPresenter );
        }

        setFocus( panel );
    }

    public PanelDefinition addWorkbenchPart(final String title,
                                            final PartDefinition part,
                                            final PanelDefinition panel,
                                            final IsWidget partWidget) {
        final WorkbenchPanel panelPresenter = mapPanelDefinitionToPresenter.get( panel );
        if ( panelPresenter == null ) {
            throw new IllegalArgumentException( "Unable to add Part to Panel. Panel has not been created." );
        }

        WorkbenchPart partPresenter = mapPartDefinitionToPresenter.get( part );
        if ( partPresenter == null ) {
            partPresenter = factory.newWorkbenchPart( title,
                                                      part );
            partPresenter.setWrappedWidget( partWidget );
            mapPartDefinitionToPresenter.put( part,
                                              partPresenter );
        }

        panelPresenter.addPart( title,
                                part,
                                partPresenter.getPartView() );
        setFocus( panel );
        return panelPresenter.getDefinition();
    }

    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final Position position) {
        final PanelDefinition childPanel = new PanelDefinition();
        return addWorkbenchPanel( targetPanel,
                                  childPanel,
                                  position );
    }

    public PanelDefinition addWorkbenchPanel(final PanelDefinition targetPanel,
                                             final PanelDefinition childPanel,
                                             final Position position) {

        PanelDefinition newPanel = null;

        WorkbenchPanel targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        switch ( position ) {
            case ROOT :
                newPanel = root;
                break;

            case SELF :
                newPanel = targetPanelPresenter.getDefinition();
                break;

            case NORTH :
            case SOUTH :
            case EAST :
            case WEST :

                final WorkbenchPanel childPanelPresenter = factory.newWorkbenchPanel( childPanel );
                mapPanelDefinitionToPresenter.put( childPanel,
                                                   childPanelPresenter );

                targetPanelPresenter.addPanel( childPanel,
                                               childPanelPresenter.getPanelView(),
                                               position );
                newPanel = childPanelPresenter.getDefinition();
                break;

            default :
                throw new IllegalArgumentException( "Unhandled Position. Expect subsequent errors." );
        }

        setFocus( childPanel );
        return newPanel;
    }

    private void setFocus(final PanelDefinition panel) {
        workbenchPanelOnFocusEvent.fire( new WorkbenchPanelOnFocusEvent( panel ) );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPanelOnFocus(@Observes WorkbenchPanelOnFocusEvent event) {
        final PanelDefinition panel = event.getPanel();
        for ( Map.Entry<PanelDefinition, WorkbenchPanel> e : mapPanelDefinitionToPresenter.entrySet() ) {
            e.getValue().setFocus( e.getKey().equals( panel ) );
        }
    }

    @SuppressWarnings("unused")
    private void onSelectWorkbenchPartEvent(@Observes SelectWorkbenchPartEvent event) {
        final PartDefinition part = event.getPart();
        for ( Map.Entry<PanelDefinition, WorkbenchPanel> e : mapPanelDefinitionToPresenter.entrySet() ) {
            if ( e.getValue().getDefinition().getParts().contains( part ) ) {
                e.getValue().selectPart( part );
            }
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartClosedEvent(@Observes WorkbenchPartCloseEvent event) {
        final PartDefinition part = event.getPart();
        removePart( part );
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartDroppedEvent(@Observes WorkbenchPartDroppedEvent event) {
        final PartDefinition part = event.getPart();
        removePart( part );
    }

    private void removePart(final PartDefinition part) {
        factory.destroy( mapPartDefinitionToPresenter.get( part ) );
        mapPartDefinitionToPresenter.remove( part );

        WorkbenchPanel panelToRemove = null;

        for ( Map.Entry<PanelDefinition, WorkbenchPanel> e : mapPanelDefinitionToPresenter.entrySet() ) {
            final PanelDefinition definition = e.getKey();
            final WorkbenchPanel presenter = e.getValue();
            if ( presenter.getDefinition().getParts().contains( part ) ) {
                presenter.removePart( part );
                if ( !definition.isRoot() && definition.getParts().size() == 0 ) {
                    panelToRemove = presenter;
                }
                break;
            }
        }
        if ( panelToRemove != null ) {
            panelToRemove.removePanel();
            factory.destroy( panelToRemove );
            mapPanelDefinitionToPresenter.remove( panelToRemove.getDefinition() );
            root.removePanel( panelToRemove.getDefinition() );
        }
    }

    public WorkbenchPanel.View getPanelView(final PanelDefinition panel) {
        return mapPanelDefinitionToPresenter.get( panel ).getPanelView();
    }

    public WorkbenchPart.View getPartView(final PartDefinition part) {
        return mapPartDefinitionToPresenter.get( part ).getPartView();
    }

}
