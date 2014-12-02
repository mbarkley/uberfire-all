package org.uberfire.client.workbench.panels;

public interface DockingWorkbenchPanelPresenter extends WorkbenchPanelPresenter {

    /**
     * Sets the width or height of the given child panel to the given dimension, leaving room for the existing space
     * taken up by (recursively) nested panels in the NORTH, SOUTH, EAST, and WEST child positions. Width is applied
     * to EAST or WEST children; height is applied to NORTH and SOUTH children. If the requested space isn't available
     * (for instance because it is larger than the browser window, or it would make the central panel of this view
     * smaller than its minimum size) then the largest possible amount will be given to the requested child.
     * Similarly, if the requested size is less than the child's minimum width or height (as appropriate) then the
     * child will be set to its minimum.
     *
     * @param child
     *            the child panel whose size to change
     * @param pixelWidth
     *            the new width for the child panel, if it supports horizontal resizing. If null, the width will not be
     *            changed.
     * @param pixelHeight
     *            the new height for the child panel, if it supports vertical resizing. If null, the height will not be
     *            changed.
     * @return true if the given child was in fact part of this panel; false if the child was not found. The return
     *         value is not affected by whether or not the requested size was null.
     */
    boolean setChildSize( WorkbenchPanelPresenter child,
                          Integer pixelWidth,
                          Integer pixelHeight );

}
