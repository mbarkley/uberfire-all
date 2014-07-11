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

import static java.util.Collections.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

@ApplicationScoped
public class PlaceManagerImpl
implements PlaceManager {

    /** Activities that have been created by us but not destroyed (TODO: move this state tracking to ActivityManager!). */
    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<PlaceRequest, Activity>();

    /** Places that are currently open in the current perspective. */
    private final Map<PlaceRequest, PartDefinition> visibleWorkbenchParts = new HashMap<PlaceRequest, PartDefinition>();

    private final Map<PlaceRequest, Command> onOpenCallbacks = new HashMap<PlaceRequest, Command>();

    private EventBus tempBus = null;

    @Inject
    private Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;

    @Inject
    private Event<ClosePlaceEvent> workbenchPartCloseEvent;

    @Inject
    private Event<NewSplashScreenActiveEvent> newSplashScreenActiveEvent;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private PlaceHistoryHandler placeHistoryHandler;

    @Inject
    private Event<SelectPlaceEvent> selectWorkbenchPartEvent;

    @Inject
    private PanelManager panelManager;

    @Inject
    private PerspectiveManager perspectiveManager;

    private final Map<String, SplashScreenActivity> activeSplashScreens = new HashMap<String, SplashScreenActivity>();
    private final Map<String, PopupActivity> activePopups = new HashMap<String, PopupActivity>();
    private final Map<PlaceRequest, Activity> onMayCloseList = new HashMap<PlaceRequest, Activity>();

    @PostConstruct
    public void initPlaceHistoryHandler() {
        getPlaceHistoryHandler().register( this,
                                           produceEventBus(),
                                           DefaultPlaceRequest.NOWHERE );
    }

    PlaceHistoryHandler getPlaceHistoryHandler() {
        return placeHistoryHandler;
    }

    @Override
    public void goTo( final String identifier,
                      final PanelDefinition panel ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, panel );
    }

    @Override
    public void goTo( final String identifier ) {
        final DefaultPlaceRequest place = new DefaultPlaceRequest( identifier );
        goTo( place, null );
    }

    @Override
    public void goTo( PlaceRequest place ) {
        goTo( place, null );
    }

    @Override
    public void goTo( final Path path,
                      final PanelDefinition panel ) {
        goTo( new PathPlaceRequest( path ), panel );
    }

    @Override
    public void goTo( final Path path ) {
        goTo( new PathPlaceRequest( path ), null );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest,
                      final PanelDefinition panel ) {
        goTo( getPlace( path, placeRequest ), panel );
    }

    @Override
    public void goTo( final Path path,
                      final PlaceRequest placeRequest ) {
        goTo( getPlace( path, placeRequest ), null );
    }

    @Override
    public void goTo( final PlaceRequest place,
                      final PanelDefinition panel ) {
        goTo( place, panel, Commands.DO_NOTHING );
    }

    private void goTo( final PlaceRequest place,
                       final PanelDefinition panel,
                       final Command doWhenFinished ) {
        if ( place == null || place.equals( DefaultPlaceRequest.NOWHERE ) ) {
            return;
        }
        final ResolvedRequest resolved = resolveActivity( place );

        if ( resolved.getActivity() != null ) {
            final Activity activity = resolved.getActivity();
            if ( activity instanceof WorkbenchActivity ) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;

                // check if we need to open the owning perspective before launching this screen/editor
                if ( workbenchActivity.getOwningPlace() != null && getStatus( workbenchActivity.getOwningPlace() ) == PlaceStatus.CLOSE ) {
                    goTo( workbenchActivity.getOwningPlace(), null, new Command() {
                        @Override
                        public void execute() {
                            goTo( place, panel, doWhenFinished );
                        }
                    } );
                    return;
                }

                launchWorkbenchActivityAtPosition( resolved.getPlaceRequest(),
                                                   workbenchActivity,
                                                   workbenchActivity.getDefaultPosition(),
                                                   panel );
                doWhenFinished.execute();

            } else if ( activity instanceof PopupActivity ) {
                launchPopupActivity( resolved.getPlaceRequest(),
                                     (PopupActivity) activity );
                doWhenFinished.execute();

            } else if ( activity instanceof PerspectiveActivity ) {
                launchPerspectiveActivity( place, (PerspectiveActivity) activity, doWhenFinished );
            }
        } else {
            goTo( resolved.getPlaceRequest(), panel, doWhenFinished );
        }
    }

    private boolean closePlaces( final Collection<PlaceRequest> placeRequests ) {
        boolean result = true;
        for ( final PlaceRequest placeRequest : placeRequests ) {
            final Activity activity = existingWorkbenchActivities.get( placeRequest );
            if ( activity instanceof AbstractWorkbenchActivity ) {
                if ( ( (AbstractWorkbenchActivity) activity ).onMayClose() ) {
                    onMayCloseList.put( placeRequest, activity );
                } else {
                    result = false;
                    break;
                }
            }
        }

        if ( !result ) {
            onMayCloseList.clear();
        } else {
            for ( final PlaceRequest placeRequest : placeRequests ) {
                closePlace( placeRequest );
            }
        }

        return result;
    }

    /**
     * Resolves the given place request into an Activity instance, if one can be found. If not, this method substitutes
     * special "not found" or "too many" place requests when the resolution doesn't work.
     * <p>
     * The behaviour of this method is affected by the boolean-valued
     * {@code org.uberfire.client.mvp.PlaceManagerImpl.ignoreUnkownPlaces} property in {@link UberFirePreferences}.
     * 
     * @param place
     *            A non-null place request that could have originated from within application code, from within the
     *            framework, or by parsing a hash fragment from a browser history event.
     * @return a non-null ResolvedRequest, where:
     *         <ul>
     *         <li>the Activity value is either the unambiguous resolved Activity instance, or null if the activity was
     *         not resolvable; in this case, the Activity has been added to the {@link #existingWorkbenchActivities} map.
     *         <li>if there is an Activity value, the PlaceRequest represents that Activity; otherwise
     *         it is a substitute PlaceRequest that should be navigated to recursively (ultimately by another call to
     *         this method). The PlaceRequest is never null.
     *         </ul>
     *         TODO (UF-94) : make this simpler. with enough tests in place, we should experiment with doing the recursive
     *         lookup automatically.
     */
    private ResolvedRequest resolveActivity( final PlaceRequest place ) {

        final ResolvedRequest existingDestination = resolveExistingParts( place );

        if ( existingDestination != null ) {
            return existingDestination;
        }

        final Set<Activity> activities = activityManager.getActivities( place );

        if ( activities == null || activities.size() == 0 ) {
            boolean ignoreUnknown = (Boolean) UberFirePreferences.getProperty("org.uberfire.client.mvp.PlaceManagerImpl.ignoreUnkownPlaces", false);
            if ( ignoreUnknown ) {
                return new ResolvedRequest( null, PlaceRequest.NOWHERE );
            }

            System.out.println("Launching notfound activity for placeRequest " + place);
            final PlaceRequest notFoundPopup = new DefaultPlaceRequest( "workbench.activity.notfound" );
            notFoundPopup.addParameter( "requestedPlaceIdentifier", place.getIdentifier() );

            return new ResolvedRequest( null, notFoundPopup );
        } else if ( activities.size() > 1 ) {
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest( "workbench.activities.multiple" ).addParameter( "requestedPlaceIdentifier", null );

            return new ResolvedRequest( null, multiplePlaces );
        }

        Activity unambigousActivity = activities.iterator().next();
        existingWorkbenchActivities.put( place, unambigousActivity );
        return new ResolvedRequest( unambigousActivity, place );
    }

    private ResolvedRequest resolveExistingParts( final PlaceRequest place ) {
        final Activity activity = getActivity( place );

        if ( activity != null ) {
            return new ResolvedRequest( activity, place );
        }

        if ( place instanceof PathPlaceRequest ) {
            for ( final Map.Entry<PlaceRequest, PartDefinition> entry : visibleWorkbenchParts.entrySet() ) {
                if ( entry.getKey() instanceof PathPlaceRequest &&
                        ( (PathPlaceRequest) entry.getKey() ).getPath().compareTo( ( (PathPlaceRequest) place ).getPath() ) == 0 ) {
                    return new ResolvedRequest( getActivity( entry.getKey() ), entry.getKey() );
                }
            }
        }

        return null;
    }

    @Override
    public void goTo( final PartDefinition part,
                      final PanelDefinition panel ) {
        final PlaceRequest place = part.getPlace();
        if ( place == null ) {
            return;
        }
        final ResolvedRequest resolved = resolveActivity( place );

        if ( resolved.getActivity() != null ) {
            final Activity activity = resolved.getActivity();

            if ( activity instanceof WorkbenchActivity ) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
                launchWorkbenchActivityInPanel( place,
                                                workbenchActivity,
                                                part,
                                                panel );
            } else {
                throw new IllegalArgumentException( "placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel." );
            }
        } else {
            goTo( resolved.getPlaceRequest() );
        }
    }

    private PlaceRequest getPlace( final Path path,
                                   final PlaceRequest placeRequest ) {
        final PlaceRequest request = new PathPlaceRequest( path );

        for ( final Map.Entry<String, String> entry : placeRequest.getParameters().entrySet() ) {
            request.addParameter( entry.getKey(), entry.getValue() );
        }

        return request;
    }

    @Override
    public Activity getActivity( final PlaceRequest place ) {
        if ( place == null ) {
            return null;
        }
        final Activity activity = existingWorkbenchActivities.get( place );
        return activity;
    }

    @Override
    public PlaceStatus getStatus( String id ) {
        return getStatus( new DefaultPlaceRequest( id ) );
    }

    @Override
    public PlaceStatus getStatus( final PlaceRequest place ) {
        PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
        if ( currentPerspective != null && currentPerspective.getPlace().equals( place ) ) {
            return PlaceStatus.OPEN;
        }
        return resolveExistingParts( place ) != null ? PlaceStatus.OPEN : PlaceStatus.CLOSE;
    }

    @Override
    public void closePlace( final String id ) {
        closePlace( new DefaultPlaceRequest( id ) );
    }

    @Override
    public void closePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        closePlace( placeToClose, false );
    }

    @Override
    public void tryClosePlace( final PlaceRequest placeToClose,
                               final Command onAfterClose ) {
        boolean execute = false;
        if ( placeToClose == null ) {
            execute = true;
        } else {
            execute = closePlaces( Arrays.asList( placeToClose ) );
        }

        if ( execute ) {
            onAfterClose.execute();
        }
    }

    @Override
    public void forceClosePlace( final String id ) {
        forceClosePlace( new DefaultPlaceRequest( id ) );
    }

    @Override
    public void forceClosePlace( final PlaceRequest placeToClose ) {
        if ( placeToClose == null ) {
            return;
        }
        closePlace( placeToClose, true );
    }

    @Override
    public void closeAllPlaces() {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>( visibleWorkbenchParts.keySet() );
        for ( PlaceRequest placeToClose : placesToClose ) {
            closePlace( placeToClose );
        }
    }

    private boolean closeAllCurrentPanels() {
        return closePlaces( new ArrayList<PlaceRequest>( visibleWorkbenchParts.keySet() ) );
    }

    @Override
    public void registerOnOpenCallback( final PlaceRequest place,
                                        final Command command ) {
        checkNotNull( "place", place );
        checkNotNull( "command", command );
        this.onOpenCallbacks.put( place,
                                  command );
    }

    @Override
    public void unregisterOnOpenCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
        this.onOpenCallbacks.remove( place );
    }

    @Override
    public void executeOnOpenCallback( final PlaceRequest place ) {
        checkNotNull( "place", place );
        final Command callback = this.onOpenCallbacks.get( place );
        if ( callback != null ) {
            callback.execute();
        }
    }

    @Override
    public Collection<SplashScreenActivity> getActiveSplashScreens() {
        return unmodifiableCollection( activeSplashScreens.values() );
    }

    /**
     * Returns all the PlaceRequests that map to activies that are currently in the open state and accessible
     * somewhere in the current perspective.
     * 
     * @return an unmodifiable view of the current active place requests. This view may or may not update after
     * further calls into PlaceManager that modify the workbench state. It's best not to hold on to the returned
     * set; instead, call this method again for current information.
     */
    public Collection<PlaceRequest> getActivePlaceRequests() {
        return unmodifiableCollection( existingWorkbenchActivities.keySet() );
    }

    /**
     * Returns all the PathPlaceRequests that map to activies that are currently in the open state and accessible
     * somewhere in the current perspective.
     * 
     * @return an unmodifiable view of the current active place requests. This view may or may not update after
     * further calls into PlaceManager that modify the workbench state. It's best not to hold on to the returned
     * set; instead, call this method again for current information.
     */
    public Collection<PathPlaceRequest> getActivePlaceRequestsWithPath() {
        ArrayList<PathPlaceRequest> pprs = new ArrayList<PathPlaceRequest>();
        for ( final PlaceRequest placeRequest : existingWorkbenchActivities.keySet() ) {
            if ( placeRequest instanceof PathPlaceRequest ) {
                pprs.add( (PathPlaceRequest) placeRequest );
            }
        }
        return pprs;
    }

    private void launchWorkbenchActivityAtPosition( final PlaceRequest place,
                                                    final WorkbenchActivity activity,
                                                    final Position position,
                                                    final PanelDefinition _panel ) {

        if ( visibleWorkbenchParts.containsKey( place ) ) {
            selectWorkbenchPartEvent.fire( new SelectPlaceEvent( place ) );
            return;
        }

        final PartDefinition part = new PartDefinitionImpl( place );
        final PanelDefinition panel;
        if ( _panel != null ) {
            panel = _panel;
        } else {
            // TODO (hbraun): If no panel given (i.e. when using token driven nav), this falls back to the root panel definition
            panel = getPanelManager().addWorkbenchPanel( getPanelManager().getRoot(),
                                                         position );
        }

        launchWorkbenchActivityInPanel( place, activity, part, panel );
    }

    private void launchWorkbenchActivityInPanel( final PlaceRequest place,
                                                 final WorkbenchActivity activity,
                                                 final PartDefinition part,
                                                 final PanelDefinition panel ) {

        visibleWorkbenchParts.put( place, part );
        getPlaceHistoryHandler().onPlaceChange( place );
        checkPathDelete( place );

        final SplashScreenActivity splashScreen = activityManager.getSplashScreenInterceptor( place );

        UIPart uiPart = new UIPart( activity.getTitle(), activity.getTitleDecoration(), activity.getWidget() );

        getPanelManager().addWorkbenchPart( place,
                                            part,
                                            panel,
                                            activity.getMenus(),
                                            uiPart,
                                            activity.contextId() );
        if ( splashScreen != null ) {
            activeSplashScreens.put( place.getIdentifier(), splashScreen );
            newSplashScreenActiveEvent.fire( new NewSplashScreenActiveEvent() );
            splashScreen.onOpen();
        }

        activity.onOpen();
    }

    PanelManager getPanelManager() {
        return panelManager;
    }

    private void checkPathDelete( final PlaceRequest place ) {
        if ( place == null ) {
            return;
        }
        try {
            if ( (Boolean) UberFirePreferences.getProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", true ) &&
                    place instanceof PathPlaceRequest ) {
                ( (PathPlaceRequest) place ).getPath().onDelete( new Command() {
                    @Override
                    public void execute() {
                        forceClosePlace( place );
                    }
                } );
            }
        } catch ( final Exception ex ) {
        }
    }

    private void launchPopupActivity( final PlaceRequest place,
                                      final PopupActivity activity ) {

        if ( activePopups.get( place.getIdentifier() ) != null ) {
            return;
        }

        getPlaceHistoryHandler().onPlaceChange( place );
        checkPathDelete( place );

        activePopups.put( place.getIdentifier(), activity );
        activity.onOpen();
    }

    private void launchPerspectiveActivity( final PlaceRequest place,
                                            final PerspectiveActivity activity,
                                            final Command doWhenFinished ) {

        checkNotNull( "doWhenFinished", doWhenFinished );

        final PerspectiveActivity oldPerspectiveActivity = perspectiveManager.getCurrentPerspective();
        if ( oldPerspectiveActivity != null && place.equals( oldPerspectiveActivity.getPlace() ) ) {
            return;
        }

        // XXX there is a layering problem here: PerspectiveManager directs us to open activities, but we are cleaning them up.
        // both operations must be the responsibility of the same manager (probably PerspectiveManager)
        if ( closeAllCurrentPanels() ) {

            activeSplashScreens.clear();
            final SplashScreenActivity splashScreen = activityManager.getSplashScreenInterceptor( place );
            if ( splashScreen != null ) {
                activeSplashScreens.put( place.getIdentifier(), splashScreen );
                splashScreen.onOpen();
            }
            newSplashScreenActiveEvent.fire( new NewSplashScreenActiveEvent() );

            Command closeOldActivityAndExecuteChainedCallback = new Command() {
                @Override
                public void execute() {
                    if ( oldPerspectiveActivity != null ) {
                        oldPerspectiveActivity.onClose();
                        existingWorkbenchActivities.remove( oldPerspectiveActivity.getPlace() );
                        activityManager.destroyActivity( oldPerspectiveActivity );
                    }
                    doWhenFinished.execute();
                }
            };
            activity.onOpen();
            perspectiveManager.switchToPerspective( activity, closeOldActivityAndExecuteChainedCallback );
        }
    }

    private void closePlace( final PlaceRequest place, final boolean force ) {

        final Activity activity = existingWorkbenchActivities.get( place );
        if ( activity == null ) {
            return;
        }

        workbenchPartBeforeCloseEvent.fire( new BeforeClosePlaceEvent( place,
                                                                       force,
                                                                       true ) );

        activeSplashScreens.remove( place.getIdentifier() );
        activePopups.remove( place.getIdentifier() );

        if ( activity instanceof WorkbenchActivity ) {
            WorkbenchActivity activity1 = (WorkbenchActivity) activity;
            if ( force || onMayCloseList.containsKey( place ) || activity1.onMayClose() ) {
                onMayCloseList.remove( place );
                activity1.onClose();
            } else {
                return;
            }
        } else if ( activity instanceof PopupActivity ) {
            PopupActivity activity1 = (PopupActivity) activity;
            if ( force || activity1.onMayClose() ) {
                activity1.onClose();
            } else {
                return;
            }
        }

        workbenchPartCloseEvent.fire( new ClosePlaceEvent( place ) );
        panelManager.removePartForPlace( place );
        existingWorkbenchActivities.remove( place );
        visibleWorkbenchParts.remove( place );

        activityManager.destroyActivity( activity );

        if ( place instanceof PathPlaceRequest ) {
            ( (PathPlaceRequest) place ).getPath().dispose();
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus( @Observes PlaceGainFocusEvent event ) {
        final PlaceRequest place = event.getPlace();
        final Activity activity = getActivity( place );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            ( (WorkbenchActivity) activity ).onFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus( @Observes PlaceLostFocusEvent event ) {
        final Activity activity = getActivity( event.getPlace() );
        if ( activity == null ) {
            return;
        }
        if ( activity instanceof WorkbenchActivity ) {
            ( (WorkbenchActivity) activity ).onLostFocus();
        }
    }

    @Produces
    @ApplicationScoped
    EventBus produceEventBus() {
        if ( tempBus == null ) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

    /**
     * The result of an attempt to resolve a PlaceRequest to an Activity.
     */
    private static class ResolvedRequest {
        private final Activity activity;
        private final PlaceRequest placeRequest;

        public ResolvedRequest(final Activity resolvedActivity, final PlaceRequest substitutePlace ) {
            this.activity = resolvedActivity;
            this.placeRequest = substitutePlace;
        }

        public Activity getActivity() {
            return activity;
        }

        public PlaceRequest getPlaceRequest() {
            return placeRequest;
        }

        @Override
        public boolean equals( final Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            final ResolvedRequest resolvedRequest = (ResolvedRequest) o;

            if ( activity != null ? !activity.equals( resolvedRequest.activity ) : resolvedRequest.activity != null ) {
                return false;
            }
            if ( placeRequest != null ? !placeRequest.equals( resolvedRequest.placeRequest ) : resolvedRequest.placeRequest != null ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = 0;
            result = activity != null ? activity.hashCode() : 0;
            result = 31 * result + ( placeRequest != null ? placeRequest.hashCode() : 0 );
            return result;
        }

        @Override
        public String toString() {
            return "{activity=" + activity + ", placeRequest=" + placeRequest + "}";
        }
    }
}
