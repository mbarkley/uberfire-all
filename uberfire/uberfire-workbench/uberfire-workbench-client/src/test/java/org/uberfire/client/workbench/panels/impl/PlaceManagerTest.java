package org.uberfire.client.workbench.panels.impl;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.BeanProvider;
import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCDependentBean;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.UberFirePreferences;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import com.google.gwt.event.shared.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class PlaceManagerTest {

    @Mock Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;
    @Mock Event<ClosePlaceEvent> workbenchPartCloseEvent;
    @Mock Event<PlaceLostFocusEvent> workbenchPartLostFocusEvent;
    @Mock Event<NewSplashScreenActiveEvent> newSplashScreenActiveEvent;
    @Mock ActivityManager activityManager;
    @Mock PlaceHistoryHandler placeHistoryHandler;
    @Mock Event<SelectPlaceEvent> selectWorkbenchPartEvent;
    @Mock PanelManager panelManager;
    @Mock PerspectiveManager perspectiveManager;

    /**
     * This is the thing we're testing. Weeee!
     */
    @InjectMocks PlaceManagerImpl placeManager;

    /** Returned by the mock activityManager for the special "workbench.activity.notfound" place. */
    private final Activity notFoundActivity = mock(Activity.class);

    /** The setup method makes this the current place. */
    private final PlaceRequest kansas = new DefaultPlaceRequest( "kansas" );

    /** The setup method links this activity with the kansas PlaceRequest. */
    private final WorkbenchScreenActivity kansasActivity = mock( WorkbenchScreenActivity.class );

    @Before
    public void setup() {
        IOC.getBeanManager().destroyAllBeans();

        when( activityManager.getActivities( any( PlaceRequest.class ) ) ).thenReturn( singleton( notFoundActivity ) );

        // for now (and this will have to change for UF-61), PathPlaceRequest performs an IOC lookup for ObservablePath in its constructor
        // as part of UF-61, we'll need to refactor ObservablePath and PathFactory so they ask for any beans they need as constructor params.
        IOC.getBeanManager().registerBean( IOCDependentBean.newBean(
                                                                    (SyncBeanManagerImpl) IOC.getBeanManager(), ObservablePath.class, ObservablePath.class,
                                                                    null, "ObservablePath", true, new BeanProvider<ObservablePath>() {
                                                                        @Override
                                                                        public ObservablePath getInstance( CreationalContext context ) {
                                                                            final ObservablePath mockObservablePath = mock( ObservablePath.class );
                                                                            when( mockObservablePath.wrap( any( Path.class ) ) ).thenReturn( mockObservablePath );
                                                                            return mockObservablePath;
                                                                        }
                                                                    }, null ) );

        // every test starts in Kansas, with no side effect interactions recorded
        when( activityManager.getActivities( kansas ) ).thenReturn( singleton( (Activity) kansasActivity ) );
        placeManager.goTo( kansas, (PanelDefinition) null );
        resetInjectedMocks();
        reset( kansasActivity );

        // arrange for the mock PerspectiveManager to invoke the doWhenFinished callback
        doAnswer( new Answer<Void>(){
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                Command callback = (Command) invocation.getArguments()[1];
                callback.execute();
                return null;
            }} ).when( perspectiveManager ).switchToPerspective( any( PerspectiveActivity.class ), any( Command.class ) );
    }

    /**
     * Resets all the mocks that were injected into the PlaceManager under test. This should probably only be used in
     * the setup method.
     */
    @SuppressWarnings("unchecked")
    private void resetInjectedMocks() {
        reset( workbenchPartBeforeCloseEvent );
        reset( workbenchPartCloseEvent );
        reset( workbenchPartLostFocusEvent );
        reset( newSplashScreenActiveEvent );
        reset( activityManager );
        reset( placeHistoryHandler );
        reset( selectWorkbenchPartEvent );
        reset( panelManager );
        reset( perspectiveManager );
    }

    @Test
    public void testPlaceManagerGetsInitializedToADefaultPlace() throws Exception {
        placeManager.initPlaceHistoryHandler();

        verify( placeHistoryHandler ).register( any( PlaceManager.class ),
                                                any( EventBus.class ),
                                                any( PlaceRequest.class ) );
    }

    @Test
    public void testGoToNewPlaceById() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest( "oz" );
        WorkbenchScreenActivity ozActivity = mock( WorkbenchScreenActivity.class );
        when( activityManager.getActivities( oz ) ).thenReturn( singleton( (Activity) ozActivity ) );

        placeManager.goTo( oz, (PanelDefinition) null );

        verifyActivityLaunchSideEffects( oz, ozActivity );
    }

    @Test
    public void testGoToPlaceWeAreAlreadyAt() throws Exception {

        placeManager.goTo( kansas, (PanelDefinition) null );

        // note "refEq" tests equality field by field using reflection. don't read it as "reference equals!" :)
        verify( selectWorkbenchPartEvent ).fire( refEq( new SelectPlaceEvent( kansas ) ) );

        verifyNoActivityLaunchSideEffects( kansas, kansasActivity );
    }

    @Test
    public void testGoToNowhereDoesNothing() throws Exception {
        placeManager.goTo( PlaceRequest.NOWHERE, (PanelDefinition) null );

        verifyNoActivityLaunchSideEffects( kansas, kansasActivity );
    }

    // XXX would like to remove this behaviour (should throw NPE) but too many things are up in the air right now
    @Test
    public void testGoToNullDoesNothing() throws Exception {

        placeManager.goTo( (PlaceRequest) null, (PanelDefinition) null );

        verifyNoActivityLaunchSideEffects( kansas, kansasActivity );
    }

    @Test
    public void testGoToPlaceByPath() throws Exception {
        final Boolean oldOnDeletePref = (Boolean) UberFirePreferences.getProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete" );

        try {
            UberFirePreferences.setProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", true );
            Path yellowBrickRoadPath = mock( Path.class );
            PathPlaceRequest yellowBrickRoad = new PathPlaceRequest( yellowBrickRoadPath, "YellowBrickRoadID" );
            WorkbenchScreenActivity ozActivity = mock( WorkbenchScreenActivity.class );

            when( activityManager.getActivities( yellowBrickRoad ) ).thenReturn( singleton( (Activity) ozActivity ) );

            placeManager.goTo( yellowBrickRoad, (PanelDefinition) null );

            verifyActivityLaunchSideEffects( yellowBrickRoad, ozActivity );

            // special contract just for path-type place requests (subject to preference)
            verify( yellowBrickRoad.getPath() ).onDelete( any( Command.class ) );
        } finally {
            UberFirePreferences.setProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", oldOnDeletePref );
        }
    }

    @Test
    public void testGoToPlaceByPathWithOnDeleteDisabled() throws Exception {
        final Boolean oldOnDeletePref = (Boolean) UberFirePreferences.getProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete" );

        try {
            UberFirePreferences.setProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", false );
            Path yellowBrickRoadPath = mock( Path.class );
            PathPlaceRequest yellowBrickRoad = new PathPlaceRequest( yellowBrickRoadPath, "YellowBrickRoadID" );
            WorkbenchScreenActivity ozActivity = mock( WorkbenchScreenActivity.class );

            when( activityManager.getActivities( yellowBrickRoad ) ).thenReturn( singleton( (Activity) ozActivity ) );

            placeManager.goTo( yellowBrickRoad, (PanelDefinition) null );

            verifyActivityLaunchSideEffects( yellowBrickRoad, ozActivity );

            // special contract just for path-type place requests (subject to preference)
            verify( yellowBrickRoad.getPath(), never() ).onDelete( any( Command.class ) );

        } finally {
            UberFirePreferences.setProperty( "org.uberfire.client.workbench.path.automatic.close.onDelete", oldOnDeletePref );
        }
    }

    @Test
    public void testNormalCloseExistingScreenActivity() throws Exception {
        when( kansasActivity.onMayClose() ).thenReturn( true );

        placeManager.closePlace( kansas );

        verify( workbenchPartBeforeCloseEvent ).fire( refEq( new BeforeClosePlaceEvent( kansas, false, true ) ) );
        verify( workbenchPartCloseEvent ).fire( refEq( new ClosePlaceEvent( kansas ) ) );
        verify( kansasActivity ).onMayClose();
        verify( kansasActivity ).onClose();
        verify( kansasActivity, never() ).onShutdown();
        verify( activityManager ).destroyActivity( kansasActivity );
        verify( panelManager ).removePartForPlace( kansas );

        assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( kansas ));
        assertNull( placeManager.getActivity( kansas ) );
        assertFalse( placeManager.getActivePlaceRequests().contains( kansas ) );
    }

    @Test
    public void testCanceledCloseExistingScreenActivity() throws Exception {
        when( kansasActivity.onMayClose() ).thenReturn( false );

        placeManager.closePlace( kansas );

        verify( workbenchPartBeforeCloseEvent ).fire( refEq( new BeforeClosePlaceEvent( kansas, false, true ) ) );
        verify( workbenchPartCloseEvent, never() ).fire( refEq( new ClosePlaceEvent( kansas ) ) );
        verify( kansasActivity ).onMayClose();
        verify( kansasActivity, never() ).onClose();
        verify( kansasActivity, never() ).onShutdown();
        verify( activityManager, never() ).destroyActivity( kansasActivity );
        verify( panelManager, never() ).removePartForPlace( kansas );

        assertEquals( PlaceStatus.OPEN, placeManager.getStatus( kansas ));
        assertSame( kansasActivity, placeManager.getActivity( kansas ) );
        assertTrue( placeManager.getActivePlaceRequests().contains( kansas ) );
    }

    @Test
    public void testForceCloseExistingScreenActivity() throws Exception {
        placeManager.forceClosePlace( kansas );

        verify( workbenchPartBeforeCloseEvent ).fire( refEq( new BeforeClosePlaceEvent( kansas, true, true ) ) );
        verify( workbenchPartCloseEvent ).fire( refEq( new ClosePlaceEvent( kansas ) ) );
        verify( kansasActivity, never() ).onMayClose();
        verify( kansasActivity ).onClose();
        verify( kansasActivity, never() ).onShutdown();
        verify( activityManager ).destroyActivity( kansasActivity );
        verify( panelManager ).removePartForPlace( kansas );

        assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( kansas ));
        assertNull( placeManager.getActivity( kansas ) );
        assertFalse( placeManager.getActivePlaceRequests().contains( kansas ) );
    }

    /**
     * Tests the basics of launching a perspective. We call it "empty" because this perspective doesn't have any panels
     * or parts in its definition.
     */
    @Test
    public void testLaunchingEmptyPerspective() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock( PerspectiveActivity.class );
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest( "oz_perspective" );
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when( activityManager.getActivities( ozPerspectivePlace ) ).thenReturn( singleton( (Activity) ozPerspectiveActivity ) );
        when( ozPerspectiveActivity.getDefaultPerspectiveLayout() ).thenReturn( ozPerspectiveDef );
        when( ozPerspectiveActivity.getPlace() ).thenReturn( ozPerspectivePlace );

        placeManager.goTo( ozPerspectivePlace );

        // verify perspective changed to oz
        verify( perspectiveManager ).switchToPerspective( eq( ozPerspectiveActivity ), any( Command.class) );
        verify( ozPerspectiveActivity ).onOpen();
        assertEquals( PlaceStatus.OPEN, placeManager.getStatus( ozPerspectivePlace ) );
        assertTrue( placeManager.getActivePlaceRequests().contains( ozPerspectivePlace ) );
        assertEquals( ozPerspectiveActivity, placeManager.getActivity( ozPerspectivePlace ) );
    }

    @Test
    public void testSwitchingPerspectives() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock( PerspectiveActivity.class );
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest( "oz_perspective" );
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when( activityManager.getActivities( ozPerspectivePlace ) ).thenReturn( singleton( (Activity) ozPerspectiveActivity ) );
        when( ozPerspectiveActivity.getDefaultPerspectiveLayout() ).thenReturn( ozPerspectiveDef );
        when( ozPerspectiveActivity.getPlace() ).thenReturn( ozPerspectivePlace );

        // we'll pretend we started in kansas
        PerspectiveActivity kansasPerspectiveActivity = mock( PerspectiveActivity.class );
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( kansasPerspectiveActivity );

        placeManager.goTo( ozPerspectivePlace );

        // verify proper shutdown of kansas
        InOrder inOrder = inOrder( activityManager, kansasPerspectiveActivity );
        inOrder.verify( kansasPerspectiveActivity ).onClose();
        inOrder.verify( activityManager ).destroyActivity( kansasPerspectiveActivity );
    }

    @Test
    public void testSwitchingFromPerspectiveToSelf() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock( PerspectiveActivity.class );
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest( "oz_perspective" );
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when( activityManager.getActivities( ozPerspectivePlace ) ).thenReturn( singleton( (Activity) ozPerspectiveActivity ) );
        when( ozPerspectiveActivity.getDefaultPerspectiveLayout() ).thenReturn( ozPerspectiveDef );
        when( ozPerspectiveActivity.getPlace() ).thenReturn( ozPerspectivePlace );

        // we'll pretend we started in oz
        when( perspectiveManager.getCurrentPerspective() ).thenReturn( ozPerspectiveActivity );

        placeManager.goTo( ozPerspectivePlace );

        // verify no side effects (should stay put)
        verify( ozPerspectiveActivity, never() ).onOpen();
        verify( perspectiveManager, never() ).switchToPerspective( any( PerspectiveActivity.class ), any( Command.class ) );
    }

    @Test
    public void testLaunchingActivityTiedToDifferentPerspective() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock( PerspectiveActivity.class );
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest( "oz_perspective" );
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when( activityManager.getActivities( ozPerspectivePlace ) ).thenReturn( singleton( (Activity) ozPerspectiveActivity ) );
        when( ozPerspectiveActivity.getDefaultPerspectiveLayout() ).thenReturn( ozPerspectiveDef );
        when( ozPerspectiveActivity.getPlace() ).thenReturn( ozPerspectivePlace );

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest( "emerald_city" );
        WorkbenchScreenActivity emeraldCityActivity = mock( WorkbenchScreenActivity.class );
        when( activityManager.getActivities( emeraldCityPlace ) ).thenReturn( singleton( (Activity) emeraldCityActivity ) );
        when( emeraldCityActivity.getOwningPlace() ).thenReturn( ozPerspectivePlace );

        placeManager.goTo( emeraldCityPlace, (PanelDefinition) null );

        // verify perspective changed to oz
        verify( perspectiveManager ).switchToPerspective( eq( ozPerspectiveActivity ), any( Command.class) );
        assertEquals( PlaceStatus.OPEN, placeManager.getStatus( ozPerspectivePlace ) );

        // verify perspective opened before the activity that launches inside it
        InOrder inOrder = inOrder( ozPerspectiveActivity, emeraldCityActivity );
        inOrder.verify( ozPerspectiveActivity ).onOpen();
        inOrder.verify( emeraldCityActivity ).onOpen();

        // and the workbench activity should have launched (after the perspective change)
        verifyActivityLaunchSideEffects( emeraldCityPlace, emeraldCityActivity );
    }

    // TODO test going to splash screens, including this special side effect
    // activityManager.getSplashScreenInterceptor( place ); // need separate test for splash screens!

    // TODO test closing a splash screen, including the side effect of removing it from the activeSplashScreens map

    // TODO test going to popup screens

    // TODO test going to an unresolvable/unknown place

    // TODO test going to a place with a specific target panel

    // TODO test closing all panels when there are a variety of different types of panels open

    // TODO compare/contrast closeAllPlaces with closeAllCurrentPanels (former is public API; latter is called before launching a new perspective)

    /**
     * Verifies that all the expected side effects of an activity launch have happened.
     * 
     * @param placeRequest
     *            The place request that was passed to some variant of PlaceManager.goTo().
     * @param activity
     *            <b>A Mockito mock<b> of the activity that was resolved for <tt>placeRequest</tt>.
     */
    private void verifyActivityLaunchSideEffects(PlaceRequest placeRequest, WorkbenchScreenActivity activity) {

        // as of UberFire 0.4. this event only happens if the place is already visible.
        // it might be be better if the event was fired unconditionally. needs investigation.
        verify( selectWorkbenchPartEvent, never() ).fire( any( SelectPlaceEvent.class ) );

        // we know the activity was created (or we wouldn't be here), but should verify that only happened one time
        verify( activityManager, times( 1 ) ).getActivities( placeRequest );

        // contract between PlaceManager and PanelManager
        verify( panelManager ).addWorkbenchPanel( panelManager.getRoot(), null );

        // contract between PlaceManager and PlaceHistoryHandler
        verify( placeHistoryHandler ).onPlaceChange( placeRequest );

        // state changes in PlaceManager itself (contract between PlaceManager and everyone)
        assertTrue(
                   "Actual place requests: " + placeManager.getActivePlaceRequests(),
                   placeManager.getActivePlaceRequests().contains( placeRequest ) );
        assertSame( activity, placeManager.getActivity( placeRequest ) );
        assertEquals( PlaceStatus.OPEN, placeManager.getStatus( placeRequest ) );

        // contract between PlaceManager and Activity
        verify( activity, never() ).onStartup( any( PlaceRequest.class ) ); // this is ActivityManager's job now
        verify( activity, times( 1 ) ).onOpen();
    }

    /**
     * Verifies that the "place change" side effects have not happened, and that the given activity is still current.
     * 
     * @param expectedCurrentPlace
     *            The place request that placeManager should still consider "current."
     * @param activity
     *            <b>A Mockito mock<b> of the activity tied to <tt>expectedCurrentPlace</tt>.
     */
    private void verifyNoActivityLaunchSideEffects(PlaceRequest expectedCurrentPlace, WorkbenchScreenActivity activity) {

        // contract between PlaceManager and PanelManager
        verify( panelManager, never() ).addWorkbenchPanel( panelManager.getRoot(), null );

        // contract between PlaceManager and PlaceHistoryHandler
        verify( placeHistoryHandler, never() ).onPlaceChange( any( PlaceRequest.class ) );

        // state changes in PlaceManager itself (contract between PlaceManager and everyone)
        assertTrue(
                   "Actual place requests: " + placeManager.getActivePlaceRequests(),
                   placeManager.getActivePlaceRequests().contains( expectedCurrentPlace ) );
        assertSame( activity, placeManager.getActivity( expectedCurrentPlace ) );
        assertEquals( PlaceStatus.OPEN, placeManager.getStatus( expectedCurrentPlace ) );

        // contract between PlaceManager and Activity
        verify( activity, never() ).onStartup( any( PlaceRequest.class ) );
        verify( activity, never() ).onOpen();
    }

}
