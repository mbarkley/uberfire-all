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
package org.uberfire.client.workbench.widgets.notfound;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
@Named("uf.workbench.activity.notfound")
public class ActivityNotFoundPresenter extends AbstractPopupActivity {

    @Inject
    //Constructor injection for testing
    public ActivityNotFoundPresenter( final PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public String getTitle() {
        return "Activity not found";
    }

    @Override
    public IsWidget getWidget() {
        return view;
    }

    @Override
    public Collection<String> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }

    @Override
    public String getSignatureId() {
        return "org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter";
    }

    public interface View
            extends
            UberView<ActivityNotFoundPresenter> {

        void setRequestedPlaceIdentifier( final String requestedPlaceIdentifier );

    }

    @Inject
    private View view;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private PlaceRequest place;

    @Override
    public void onStartup( final PlaceRequest place ) {
        super.onStartup( place );
        this.place = place;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @Override
    public void onOpen() {
        super.onOpen();
        final String identifier = place.getParameter( "requestedPlaceIdentifier", null );
        view.setRequestedPlaceIdentifier( identifier );
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

}
