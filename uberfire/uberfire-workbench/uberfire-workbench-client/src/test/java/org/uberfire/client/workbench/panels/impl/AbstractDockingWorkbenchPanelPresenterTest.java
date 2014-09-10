package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter.View;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.ContextDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.ContextDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behaviours required by all subclasses of AbstractDockingWorkbenchPanelPresenter.
 */
public abstract class AbstractDockingWorkbenchPanelPresenterTest {

    @Mock protected ActivityManager mockActivityManager;
    @Mock protected PerspectiveManager mockPerspectiveManager;
    @Mock protected Event<MaximizePlaceEvent> maximizePanelEvent;
    @Mock protected View mockPartView;
    @Mock protected WorkbenchPartPresenter mockPartPresenter;
    @Mock protected ContextActivity perspectiveContextActivity;

    protected final PerspectiveDefinition panelManagerPerspectiveDefinition = new PerspectiveDefinitionImpl();
    protected final PartDefinition partPresenterPartDefinition = new PartDefinitionImpl( new DefaultPlaceRequest( "belongs_to_mockPartPresenter" ) );
    protected final PanelDefinition panelPresenterPanelDefinition = new PanelDefinitionImpl();
    protected final ContextDefinition perspectiveContextDefinition = new ContextDefinitionImpl( new DefaultPlaceRequest( "Perspective Context" ) );

    /**
     * The individual test classes that extend this base class implement this method by returning the implementation of
     * AbstractDockingWorkbenchPanelPresenter that they want to test. The protected mock objects set up by this base class
     * should be given to the presenter's constructor.
     */
    abstract AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest();

    @Before
    public void setUp() {
        when( mockPerspectiveManager.getLivePerspectiveDefinition() ).thenReturn( panelManagerPerspectiveDefinition );
        panelManagerPerspectiveDefinition.setContextDefinition( perspectiveContextDefinition );
        when( mockActivityManager.getActivity( ContextActivity.class, perspectiveContextDefinition.getPlace() ) ).thenReturn( perspectiveContextActivity );
        when( mockPartView.getPresenter() ).thenReturn( mockPartPresenter );
        when( mockPartPresenter.getDefinition() ).thenReturn( partPresenterPartDefinition );
    }

    @Test
    public void addingPartShouldUpdateDefinition() throws Exception {
        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.addPart( mockPartPresenter );

        assertSame( panelPresenter.getDefinition(), mockPartPresenter.getDefinition().getParentPanel() );
        assertTrue( panelPresenter.getDefinition().getParts().contains( mockPartPresenter.getDefinition() ) );
    }

    @Test
    public void removingPartShouldUpdateDefinition() throws Exception {
        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.addPart( mockPartPresenter );
        panelPresenter.removePart( mockPartPresenter.getDefinition() );

        assertNull( mockPartPresenter.getDefinition().getParentPanel() );
        assertFalse( panelPresenter.getDefinition().getParts().contains( mockPartPresenter.getDefinition() ) );
    }

    @Test
    public void removingLastPartFromPanelShouldRemovePanelToo() throws Exception {
        WorkbenchPanelPresenter westChildPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );
        WorkbenchPanelPresenter parentPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );

        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();

        panelPresenter.setParent( parentPanelPresenter );
        panelPresenter.addPanel( westChildPanelPresenter, CompassPosition.WEST );
        panelPresenter.addPart( mockPartPresenter );

        panelPresenter.removePart( mockPartPresenter.getDefinition() );

        // the now-empty panel should have removed itself from its parent
        verify( parentPanelPresenter ).removePanel( panelPresenter );
    }

    @Test
    public void childrenOfRemovedPanelsShouldBeRescued() throws Exception {
        WorkbenchPanelPresenter westChildPanelPresenter = mock( SimpleWorkbenchPanelPresenter.class );
        WorkbenchPanelPresenter westChildChild = mock( SimpleWorkbenchPanelPresenter.class );

        when( westChildPanelPresenter.getPanels() ).thenReturn( ImmutableMap.of( (Position) CompassPosition.WEST, westChildChild) );

        AbstractDockingWorkbenchPanelPresenter<?> panelPresenter = getPresenterToTest();
        panelPresenter.addPanel( westChildPanelPresenter, CompassPosition.WEST );
        panelPresenter.removePanel( westChildPanelPresenter );

        // the child of the removed child should have been placed into our WEST child slot
        assertEquals( CompassPosition.WEST, panelPresenter.positionOf( westChildChild ) );
    }

}
