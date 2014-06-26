package org.uberfire.client.workbench;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.workbench.model.PerspectiveDefinition;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * The default layout implementation.
 *
 * @author Heiko Braun
 * @date 05/06/14
 */
@ApplicationScoped
public class WorkbenchLayoutImpl implements WorkbenchLayout {

    /**
     * Top-level widget of the whole workbench layout. This panel contains the nested container panels for headers,
     * footers, and the current perspective. During a normal startup of UberFire, this panel would be added directly to
     * the RootLayoutPanel.
     */
    @Inject // using @Inject here because a real HeaderPanel can't be constructed in a GwtMockito test
    private HeaderPanel root;

    /**
     * The panel within which the current perspective's root view resides. This panel lasts the lifetime of the app; it's
     * cleared and repopulated with the new perspective's root view each time
     * {@link org.uberfire.client.workbench.PanelManager#setPerspective(PerspectiveDefinition)} gets called.
     */
    private final Panel perspectiveRootContainer = new SimpleLayoutPanel();

    /**
     * The panel within which the current perspective's header widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setHeaderContents(java.util.List)} gets called.
     */
    private final Panel headerPanel = new FlowPanel();

    /**
     * The panel within which the current perspective's footer widgets reside. This panel lasts the lifetime of the app;
     * it's cleared and repopulated with the new perspective's root view each time
     * {@link #setFooterContents(java.util.List)} gets called. The actual panel that's used for this is specified by the
     * concrete subclass's constructor.
     */
    private final Panel footerPanel = new FlowPanel();

    @Inject
    private WorkbenchDragAndDropManager dndManager;

    /**
     * We read the drag boundary panel out of this, and sandwich it between the root panel and the perspective container panel.
     */
    @Inject
    private WorkbenchPickupDragController dragController;

    @Inject
    private PanelManager panelManager;

    @Override
    public IsWidget getRoot() {
        return root;
    }

    @Override
    public HasWidgets getPerspectiveContainer() {
        return perspectiveRootContainer;
    }

    @Override
    public void setHeaderContents( List<Header> headers ) {
        headerPanel.clear();
        for ( Header h : headers ) {
            headerPanel.add( h );
        }
    }

    @Override
    public void setFooterContents( List<Footer> footers ) {
        footerPanel.clear();
        for ( Footer f : footers ) {
            footerPanel.add( f );
        }
    }

    @Override
    public void onBootstrap() {
        dndManager.unregisterDropControllers();

        AbsolutePanel dragBoundary = dragController.getBoundaryPanel();
        dragBoundary.add( perspectiveRootContainer );

        root.setHeaderWidget( headerPanel );
        root.setFooterWidget( footerPanel );
        root.setContentWidget( dragBoundary );
    }

    @Override
    public void onResize() {
        resizeTo( Window.getClientWidth(), Window.getClientHeight() );
    }

    @Override
    public void resizeTo(int width, int height) {
        root.setPixelSize( width, height );

        int leftoverHeight = height - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight();

        dragController.getBoundaryPanel().setPixelSize( width, leftoverHeight );
        perspectiveRootContainer.setPixelSize( width, leftoverHeight );
    }

}
