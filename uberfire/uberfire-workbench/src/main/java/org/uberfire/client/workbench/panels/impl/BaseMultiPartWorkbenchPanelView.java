package org.uberfire.client.workbench.panels.impl;

import javax.annotation.PostConstruct;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.ContextPanel;
import org.uberfire.workbench.model.PartDefinition;

public abstract class BaseMultiPartWorkbenchPanelView<P extends BaseMultiPartWorkbenchPanelPresenter>
        extends BaseWorkbenchPanelView<P> {

    protected RequiresResizeFlowPanel container = new RequiresResizeFlowPanel();
    protected ContextPanel contextWidget = new ContextPanel();
    protected MultiPartWidget widget;

    protected abstract MultiPartWidget setupWidget();

    @PostConstruct
    private void setupDragAndDrop() {
        widget = setupWidget();
        widget.asWidget().getElement().getStyle().setOverflow( Style.Overflow.HIDDEN );
        container.add( contextWidget );
        container.add( widget );
        initWidget( container );
        dndManager.registerDropController( this, factory.newDropController( this ) );
    }

    @Override
    public void init( final P presenter ) {
        this.presenter = presenter;
        widget.setPresenter( presenter );
        widget.setDndManager( dndManager );
        setupContext( null );
    }

    @Override
    public void clear() {
        widget.clear();
    }

    @Override
    public void addPart( final WorkbenchPartPresenter.View view ) {
        widget.addPart( view );
    }

    @Override
    public void changeTitle( final PartDefinition part,
                             final String title,
                             final IsWidget titleDecoration ) {
        widget.changeTitle( part, title, titleDecoration );
    }

    @Override
    public void selectPart( final PartDefinition part ) {
        setupContext( part );
        widget.selectPart( part );
    }

    private void setupContext( final PartDefinition part ) {
        final ContextActivity context = presenter.resolveContext( part );
        if ( context != null ) {
            final UIPart contextUiPart = new UIPart( context.getTitle(), context.getTitleDecoration(), context.getWidget() );
            if ( contextWidget.getUiPart() == null ) {
                context.onAttach( presenter.getDefinition() );
                contextWidget.setUiPart( contextUiPart );
            } else if ( !contextUiPart.getWidget().equals( contextWidget.getUiPart().getWidget() ) ) {
                context.onAttach( presenter.getDefinition() );
                contextWidget.setUiPart( contextUiPart );
            }
        }
    }

    @Override
    public void removePart( final PartDefinition part ) {
        widget.remove( part );
    }

    @Override
    public void setFocus( boolean hasFocus ) {
        widget.setFocus( hasFocus );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            final int width = parent.getOffsetWidth();
            final int height = parent.getOffsetHeight();
            presenter.onResize( width, height );
            widget.onResize();
            super.onResize();
        }
    }

    public class RequiresResizeFlowPanel
            extends FlowPanel
            implements RequiresResize {

        @Override
        public void onResize() {
            for ( int i = 0; i < getWidgetCount(); i++ ) {
                final Widget activeWidget = getWidget( i );
                if ( activeWidget instanceof RequiresResize ) {
                    ( (RequiresResize) activeWidget ).onResize();
                }
            }
        }
    }

}
