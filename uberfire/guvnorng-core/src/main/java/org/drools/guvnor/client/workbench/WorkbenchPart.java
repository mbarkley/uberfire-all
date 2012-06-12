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
package org.drools.guvnor.client.workbench;

import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseEvent;
import org.drools.guvnor.client.workbench.widgets.events.ActivityCloseHandler;
import org.drools.guvnor.client.workbench.widgets.events.HasCloseActivityHandlers;
import org.drools.guvnor.client.workbench.widgets.events.HasWorkbenchPartHideHandlers;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideEvent;
import org.drools.guvnor.client.workbench.widgets.events.WorkbenchPartHideHandler;
import org.drools.guvnor.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class WorkbenchPart extends SimpleLayoutPanel
        implements
        HasCloseHandlers<WorkbenchPart>,
        HasCloseActivityHandlers,
        HasSelectionHandlers<WorkbenchPart>,
        HasWorkbenchPartHideHandlers {

    private String      title;
    private ScrollPanel sp = new ScrollPanel();

    public WorkbenchPart(final Widget widget,
                         final String title) {
        this.title = title;
        sp.setWidget( widget );
        setWidget( sp );
    }

    public void close() {
        PanelManager.getInstance().removeWorkbenchPart( this );
    }

    public String getPartTitle() {
        return title;
    }

    @Override
    public HandlerRegistration addCloseHandler(CloseHandler<WorkbenchPart> handler) {
        return addHandler( handler,
                           CloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addActivityCloseHandler(ActivityCloseHandler handler) {
        return addHandler( handler,
                           ActivityCloseEvent.getType() );
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<WorkbenchPart> handler) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }

    @Override
    public HandlerRegistration addWorkbenchPartHideHandler(WorkbenchPartHideHandler handler) {
        return addHandler( handler,
                           WorkbenchPartHideEvent.getType() );
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            sp.setPixelSize( parent.getOffsetWidth(),
                             parent.getOffsetHeight() );
        }
        super.onResize();
    }
}
