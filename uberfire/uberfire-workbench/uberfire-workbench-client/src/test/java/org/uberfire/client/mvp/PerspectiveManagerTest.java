package org.uberfire.client.mvp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveManagerTest {

    @Mock PlaceManager placeManager;
    @Mock PanelManager panelManager;
    @Mock WorkbenchServicesProxy wbServices;
    @Mock Event<PerspectiveChange> perspectiveChangeEvent;

    @InjectMocks PerspectiveManagerImpl perspectiveManager;

    // useful mocks provided by setup method
    private PerspectiveDefinition ozDefinition;
    private PerspectiveActivity oz;
    private Command doWhenFinished;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        ozDefinition = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );

        oz = mock( PerspectiveActivity.class );
        when( oz.getDefaultPerspectiveLayout() ).thenReturn( ozDefinition );
        when( oz.getIdentifier() ).thenReturn( "oz_perspective" );
        when( oz.isTransient() ).thenReturn( true );

        doWhenFinished = mock( Command.class );

        // simulate "finished saving" callback on wbServices.save()
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                Command callback = (Command) invocation.getArguments()[1];
                callback.execute();
                return null;
            }
        } ).when( wbServices ).save( any( PerspectiveDefinition.class ), any( Command.class ) );

        // simulate "no saved state found" result on wbServices.loadPerspective()
        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( InvocationOnMock invocation ) throws Throwable {
                ParameterizedCommand<?> callback = (ParameterizedCommand<?>) invocation.getArguments()[1];
                callback.execute( null );
                return null;
            }
        } ).when( wbServices ).loadPerspective( anyString(), any( ParameterizedCommand.class ) );

        // XXX should look at why PanelManager needs to return an alternative panel sometimes.
        // would be better if addWorkbenchPanel returned void.
        when( panelManager.addWorkbenchPanel( any( PanelDefinition.class ),
                                              any( PanelDefinition.class ),
                                              any( Position.class ) ) ).thenAnswer( new Answer<PanelDefinition>() {
                                                  @Override
                                                  public PanelDefinition answer( InvocationOnMock invocation ) {
                                                      return (PanelDefinition) invocation.getArguments()[1];
                                                  }
                                              } );
    }

    @Test
    public void shouldReportNullPerspectiveInitially() throws Exception {
        assertNull( perspectiveManager.getCurrentPerspective() );
    }

    @Test
    public void shouldReportNewPerspectiveAsCurrentAfterSwitching() throws Exception {
        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        assertSame( oz, perspectiveManager.getCurrentPerspective() );
    }

    @Test
    public void shouldSaveOldNonTransientPerspectiveStateWhenSwitching() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );

        PerspectiveActivity kansas = mock( PerspectiveActivity.class );
        when( kansas.getDefaultPerspectiveLayout() ).thenReturn( kansasDefinition );
        when( kansas.getIdentifier() ).thenReturn( "kansas_perspective" );
        when( kansas.isTransient() ).thenReturn( false );

        perspectiveManager.switchToPerspective( kansas, doWhenFinished );
        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( wbServices ).save( eq( kansasDefinition ), any( Command.class ) );
    }

    @Test
    public void shouldNotSaveOldTransientPerspectiveStateWhenSwitching() throws Exception {
        PerspectiveDefinition kansasDefinition = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );

        PerspectiveActivity kansas = mock( PerspectiveActivity.class );
        when( kansas.getDefaultPerspectiveLayout() ).thenReturn( kansasDefinition );
        when( kansas.getIdentifier() ).thenReturn( "kansas_perspective" );
        when( kansas.isTransient() ).thenReturn( true );

        perspectiveManager.switchToPerspective( kansas, doWhenFinished );
        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( wbServices, never() ).save( eq( kansasDefinition ), any( Command.class ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldLoadNewNonTransientPerspectiveState() throws Exception {
        when( oz.isTransient() ).thenReturn( false );

        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( wbServices ).loadPerspective( eq( "oz_perspective" ), any( ParameterizedCommand.class ) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotLoadNewTransientPerspectiveState() throws Exception {
        when( oz.isTransient() ).thenReturn( true );

        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( wbServices, never() ).loadPerspective( eq( "oz_perspective" ), any( ParameterizedCommand.class ) );
    }

    @Test
    public void shouldExecuteCallbackWhenDoneLaunchingPerspective() throws Exception {
        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( doWhenFinished ).execute();
    }

    @Test
    public void shouldFireEventWhenLaunchingNewPerspective() throws Exception {
        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( perspectiveChangeEvent ).fire( refEq( new PerspectiveChange( ozDefinition, null, "oz_perspective" )) );
    }

    @Test
    public void shouldAddAllPanelsToPanelManager() throws Exception {
        PanelDefinition westPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        PanelDefinition eastPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        PanelDefinition northPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        PanelDefinition southPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        PanelDefinition southWestPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );

        ozDefinition.getRoot().appendChild( CompassPosition.WEST, westPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.EAST, eastPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.NORTH, northPanel );
        ozDefinition.getRoot().appendChild( CompassPosition.SOUTH, southPanel );
        southPanel.appendChild( CompassPosition.WEST, southWestPanel );

        // we assume this will be set correctly (verified elsewhere)
        when( panelManager.getRoot() ).thenReturn( ozDefinition.getRoot() );

        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), westPanel, CompassPosition.WEST );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), eastPanel, CompassPosition.EAST );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), northPanel, CompassPosition.NORTH );
        verify( panelManager ).addWorkbenchPanel( ozDefinition.getRoot(), southPanel, CompassPosition.SOUTH );
        verify( panelManager ).addWorkbenchPanel( southPanel, southWestPanel, CompassPosition.WEST );
    }

    @Test
    public void shouldLaunchPartsFoundInPanels() throws Exception {
        PartDefinitionImpl rootPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "rootPart1" ) );
        PartDefinitionImpl southPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "southPart1" ) );
        PartDefinitionImpl southPart2 = new PartDefinitionImpl( new DefaultPlaceRequest( "southPart2" ) );
        PartDefinitionImpl southWestPart1 = new PartDefinitionImpl( new DefaultPlaceRequest( "southWestPart1" ) );
        PartDefinitionImpl southWestPart2 = new PartDefinitionImpl( new DefaultPlaceRequest( "southWestPart2" ) );

        ozDefinition.getRoot().addPart( rootPart1 );

        PanelDefinition southPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        southPanel.addPart( southPart1 );
        southPanel.addPart( southPart2 );
        ozDefinition.getRoot().appendChild( CompassPosition.SOUTH, southPanel );

        PanelDefinition southWestPanel = new PanelDefinitionImpl( PanelType.MULTI_LIST );
        southWestPanel.addPart( southWestPart1 );
        southWestPanel.addPart( southWestPart2 );
        southPanel.appendChild( CompassPosition.WEST, southWestPanel );

        // we assume this will be set correctly (verified elsewhere)
        when( panelManager.getRoot() ).thenReturn( ozDefinition.getRoot() );

        perspectiveManager.switchToPerspective( oz, doWhenFinished );

        InOrder inOrder = inOrder( placeManager );
        inOrder.verify( placeManager ).goTo( rootPart1, ozDefinition.getRoot() );
        inOrder.verify( placeManager ).goTo( southPart1, southPanel );
        inOrder.verify( placeManager ).goTo( southPart2, southPanel );
        inOrder.verify( placeManager ).goTo( southWestPart1, southWestPanel );
        inOrder.verify( placeManager ).goTo( southWestPart2, southWestPanel );
    }

}
