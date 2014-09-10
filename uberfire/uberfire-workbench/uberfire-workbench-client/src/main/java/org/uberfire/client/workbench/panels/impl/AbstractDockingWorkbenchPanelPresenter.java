package org.uberfire.client.workbench.panels.impl;

import static org.uberfire.client.workbench.panels.impl.AbstractDockingWorkbenchPanelView.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;

import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.workbench.events.MaximizePlaceEvent;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;

/**
 * Implements the behaviour for panel presenters that support adding child panels in {@link CompassPosition} positions.
 */
public abstract class AbstractDockingWorkbenchPanelPresenter<P extends AbstractWorkbenchPanelPresenter<P>>
extends AbstractWorkbenchPanelPresenter<P>  {

    public AbstractDockingWorkbenchPanelPresenter( WorkbenchPanelView<P> view,
                                                   PerspectiveManager perspectiveManager,
                                                   Event<MaximizePlaceEvent> maximizePanelEvent ) {
        super( view,
               perspectiveManager,
               maximizePanelEvent );
    }

    /**
     * Forwards requests to existing child panels in case there is already a child panel in the requested position.
     * Otherwise behaves exactly like the superclass.
     */
    @Override
    public void addPanel( WorkbenchPanelPresenter newChild,
                          Position position ) {
        WorkbenchPanelPresenter existingChild = getPanels().get( position );
        if ( existingChild != null && newChild instanceof AbstractDockingWorkbenchPanelPresenter ) {
            Integer existingChildSize = initialWidthOrHeight( (CompassPosition) position, existingChild.getDefinition() );
            Integer newChildSize = initialWidthOrHeight( (CompassPosition) position, newChild.getDefinition() );
            if ( existingChildSize == null ) {
                existingChildSize = 0;
            }
            if ( newChildSize == null ) {
                newChildSize = 0;
            }

            removePanel( existingChild );
            super.addPanel( newChild, position );
            newChild.addPanel( existingChild, position );

            getPanelView().setChildSize( (DockingWorkbenchPanelView<?>) newChild.getPanelView(),
                                         newChildSize + existingChildSize );
        } else {
            super.addPanel( newChild, position );
        }
    }

    /**
     * Checks for existing child panels of the panel to be removed, and reparents them to this panel in the position of
     * the child panel. Once the child panels are safely out of the way, the actual panel removal is done by a call to
     * super.removePanel().
     */
    @Override
    public boolean removePanel( WorkbenchPanelPresenter child ) {
        if ( child instanceof AbstractDockingWorkbenchPanelPresenter ) {

            Position removedPosition = positionOf( child );
            if ( removedPosition == null ) {
                return false;
            }

            List<AbstractDockingWorkbenchPanelPresenter<?>> rescuedOrphans = new ArrayList<AbstractDockingWorkbenchPanelPresenter<?>>();
            AbstractDockingWorkbenchPanelPresenter<?> dockingChild = (AbstractDockingWorkbenchPanelPresenter<?>) child;
            for ( Map.Entry<Position, WorkbenchPanelPresenter> entry : dockingChild.getPanels().entrySet() ) {
                dockingChild.removeWithoutOrphanRescue( entry.getValue() );
                rescuedOrphans.add( (AbstractDockingWorkbenchPanelPresenter<?>) entry.getValue() );
                // TODO multiple off-axis orphans need special treatment
                // for example: if the NORTH panel has EAST and WEST children, we need to take the EAST one as a direct child
                // to the NORTH of us, and reparent the other to the WEST of that one (or vice-versa)
                // on the other hand, if there was only one EAST or WEST child, we can just stick it in as our new NORTH child
            }
            super.removePanel( dockingChild );
            for ( AbstractDockingWorkbenchPanelPresenter<?> rescued : rescuedOrphans ) {
                addPanel( rescued, removedPosition );
            }

            return true;

        } else {
            return super.removePanel( child );
        }
    }

    /**
     * Removes the given child panel without modifying child attachments at all. This is used d
     * @param child
     */
    private boolean removeWithoutOrphanRescue( WorkbenchPanelPresenter child ) {
        return super.removePanel( child );
    }

    @Override
    public boolean removePart( PartDefinition part ) {
        if ( super.removePart( part ) ) {
            final PanelDefinition panelDef = getDefinition();

            // if we are not the root and we have become empty, we remove ourselves from the panel hierarchy,
            // preserving all child panels
            if ( panelDef.getParts().isEmpty() && getParent() != null ) {
                getParent().removePanel( this );
            }
            return true;
        }
        return false;
    }

    @Override
    public DockingWorkbenchPanelView<P> getPanelView() {
        return (DockingWorkbenchPanelView<P>) super.getPanelView();
    }
}
