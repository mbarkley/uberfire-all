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
package org.uberfire.client.mvp;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartBeforeCloseEvent;
import org.uberfire.client.workbench.widgets.events.WorkbenchPartCloseEvent;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Base class for Pop-up Activities
 */
public abstract class AbstractPopupActivity extends AbstractActivity
        implements
        PopupActivity {

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private Event<WorkbenchPartBeforeCloseEvent> closePlaceEvent;

    @Inject
    private PopupView popup;

    public AbstractPopupActivity( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place,
                      callback );

        onStart( place );

        final IsWidget widget = getWidget();
        final IsWidget titleWidget = getTitleWidget();
        popup.init( this );

        popup.setContent( widget );
        popup.setTitle( titleWidget );

        //When pop-up is closed destroy bean to avoid memory leak
        popup.addCloseHandler( new CloseHandler<PopupPanel>() {

            @Override
            public void onClose( CloseEvent<PopupPanel> event ) {
                iocManager.destroyBean( AbstractPopupActivity.this );
            }

        } );
        popup.show();
        popup.center();

        onReveal();
    }

    @Override
    public abstract IsWidget getTitleWidget();

    @Override
    public abstract IsWidget getWidget();

    @Override
    public void onStart() {
        //Do nothing.  
    }

    @Override
    public void onStart( final PlaceRequest place ) {
        //Do nothing.  
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {
        //Do nothing.
    }

    public void close() {
        closePlaceEvent.fire( new WorkbenchPartBeforeCloseEvent( this.place ) );
    }

    @SuppressWarnings("unused")
    private void onClose( @Observes WorkbenchPartCloseEvent event ) {
        final PlaceRequest place = event.getPlace();
        if ( place.equals( this.place ) ) {
            popup.hide();
        }
    }

}
