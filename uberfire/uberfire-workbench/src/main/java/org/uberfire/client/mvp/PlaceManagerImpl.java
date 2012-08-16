/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.perspectives.Perspective;
import org.uberfire.client.workbench.perspectives.PerspectivePart;
import org.uberfire.client.workbench.widgets.events.SelectWorkbenchPartEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartLostFocusEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartOnFocusEvent;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
        implements
        PlaceManager {

    private final Map<PlaceRequest, WorkbenchActivity> existingWorkbenchActivities = new HashMap<PlaceRequest, WorkbenchActivity>();
    private final Map<PlaceRequest, WorkbenchPart>     existingWorkbenchParts      = new HashMap<PlaceRequest, WorkbenchPart>();

    private final ActivityMapper                       activityMapper;

    private EventBus                                   tempBus                     = null;

    @Inject
    private PanelManager                               panelManager;

    @Inject
    private Event<WorkbenchPartCloseEvent>             workbenchPartCloseEvent;

    private final Event<SelectWorkbenchPartEvent>      selectWorkbenchPartEvent;

    private final PlaceHistoryHandler                  placeHistoryHandler;

    PlaceRequest                                       currentPlaceRequest;

    @Inject
    public PlaceManagerImpl(ActivityMapper activityMapper,
                            PlaceHistoryHandler placeHistoryHandler,
                            Event<SelectWorkbenchPartEvent> selectWorkbenchPartEvent) {
        this.activityMapper = activityMapper;
        this.placeHistoryHandler = placeHistoryHandler;
        this.selectWorkbenchPartEvent = selectWorkbenchPartEvent;

        initPlaceHistoryHandler();
    }

    public void initPlaceHistoryHandler() {
        placeHistoryHandler.register( this,
                                      produceEventBus(),
                                      new PlaceRequest( "NOWHERE" ) );
    }

    @Override
    public void goTo(final PlaceRequest placeRequest) {

        if ( placeRequest == null ) {
            return;
        }

        final Activity activity = activityMapper.getActivity( placeRequest );
        if ( activity == null ) {
            return;
        }

        if ( activity instanceof WorkbenchActivity ) {
            revealActivity( placeRequest,
                            (WorkbenchActivity) activity );
        } else if ( activity instanceof PopupActivity ) {
            revealActivity( placeRequest,
                            (PopupActivity) activity );
        } else if ( activity instanceof PerspectiveActivity ) {
            revealActivity( placeRequest,
                            (PerspectiveActivity) activity );
        }
    }

    @Override
    public PlaceRequest getCurrentPlaceRequest() {
        if ( currentPlaceRequest != null ) {
            return currentPlaceRequest;
        } else {
            return new PlaceRequest( "NOWHERE" );
        }
    }

    @Override
    public void closeAllPlaces() {
        final List<WorkbenchPart> partsToBeClosed = new ArrayList<WorkbenchPart>( existingWorkbenchParts.values() );
        for ( WorkbenchPart part : partsToBeClosed ) {
            final WorkbenchPartBeforeCloseEvent beforeCloseEvent = new WorkbenchPartBeforeCloseEvent( part );
            onWorkbenchPartClosed( beforeCloseEvent );
        }
    }

    private void revealActivity(final PlaceRequest newPlace,
                                final WorkbenchActivity activity) {
        //If we're already showing this place exit.
        if ( existingWorkbenchActivities.containsKey( newPlace ) ) {
            final WorkbenchPart part = existingWorkbenchParts.get( newPlace );
            selectWorkbenchPartEvent.fire( new SelectWorkbenchPartEvent( part ) );
            return;
        }

        //Record new activity
        currentPlaceRequest = newPlace;
        existingWorkbenchActivities.put( newPlace,
                                         activity );

        //Reveal activity with call-back to attach to Workbench
        activity.onRevealPlace(
                new AcceptItem() {
                    public void add(String tabTitle,
                                    IsWidget widget) {
                        final WorkbenchPart part = new WorkbenchPart( widget.asWidget(),
                                                                      tabTitle );
                        existingWorkbenchParts.put( newPlace,
                                                    part );
                        panelManager.addWorkbenchPanel( part,
                                                        activity.getDefaultPosition() );
                    }
                } );

        updateHistory( newPlace );
    }

    private void revealActivity(final PlaceRequest newPlace,
                                final PopupActivity activity) {
        activity.onRevealPlace();
    }

    private void revealActivity(final PlaceRequest newPlace,
                                final PerspectiveActivity activity) {
        closeAllPlaces();
        //TODO {manstis} Need to use the Perspective's Position setting
        //TODO {manstis} What does PerspectiveActivity.onReveal() do? Might need to move onReveal up the hierarchy
        //TODO {manstis} Support for nesting of Places within the Perspective
        final Perspective perspective = activity.getPerspective();
        for ( PerspectivePart part : perspective.getParts() ) {
            goTo( part.getPlace() );
        }
        activity.onReveal();
    }

    public void updateHistory(PlaceRequest request) {
        placeHistoryHandler.onPlaceChange( request );
    }

    /**
     * Lookup the WorkbenchActivity corresponding to the WorkbenchPart
     * 
     * @param part
     * @return
     */
    @Override
    public WorkbenchActivity getActivity(final WorkbenchPart part) {
        final PlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return null;
        }
        final WorkbenchActivity activity = existingWorkbenchActivities.get( place );
        return activity;
    }

    private void onWorkbenchPartClosed(@Observes WorkbenchPartBeforeCloseEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final PlaceRequest place = getPlaceForWorkbenchPart( part );
        if ( place == null ) {
            return;
        }
        final WorkbenchActivity activity = existingWorkbenchActivities.get( place );
        if ( activity == null ) {
            return;
        }
        if ( activity.onMayClose() ) {
            activity.onClose();
            existingWorkbenchActivities.remove( place );
            existingWorkbenchParts.remove( place );
            workbenchPartCloseEvent.fire( new WorkbenchPartCloseEvent( part ) );
        }
    }

    private PlaceRequest getPlaceForWorkbenchPart(final WorkbenchPart part) {
        for ( Map.Entry<PlaceRequest, WorkbenchPart> e : existingWorkbenchParts.entrySet() ) {
            if ( e.getValue().equals( part ) ) {
                return e.getKey();
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus(@Observes WorkbenchPartOnFocusEvent event) {
        final WorkbenchPart part = event.getWorkbenchPart();
        final WorkbenchActivity activity = getActivity( part );
        if ( activity == null ) {
            return;
        }
        activity.onFocus();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus(@Observes WorkbenchPartLostFocusEvent event) {
        final WorkbenchPart part = event.getDeselectedWorkbenchPart();
        final WorkbenchActivity activity = getActivity( part );
        if ( activity == null ) {
            return;
        }
        activity.onLostFocus();
    }

    @Produces
    @ApplicationScoped
    EventBus produceEventBus() {
        if ( tempBus == null ) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

}
