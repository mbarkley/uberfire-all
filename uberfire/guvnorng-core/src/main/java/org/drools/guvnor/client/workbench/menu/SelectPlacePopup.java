package org.drools.guvnor.client.workbench.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectPlacePopup extends PopupPanel
    implements
    HasSelectionHandlers<IPlaceRequest> {

    private final VerticalPanel layout = new VerticalPanel();

    public SelectPlacePopup(Set<AbstractStaticScreenActivity> activities) {
        add( layout );

        //Sort Activities so they're always in the same sequence!
        List<AbstractStaticScreenActivity> sortedActivities = new ArrayList<AbstractStaticScreenActivity>( activities );
        Collections.sort( sortedActivities,
                          new Comparator<AbstractStaticScreenActivity>() {

                              @Override
                              public int compare(AbstractStaticScreenActivity o1,
                                                 AbstractStaticScreenActivity o2) {
                                  return o1.getTitle().compareTo( o2.getTitle() );
                              }

                          } );

        for ( final AbstractStaticScreenActivity activity : sortedActivities ) {
            Button button = new Button( activity.getTitle() );
            button.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    SelectionEvent.fire( SelectPlacePopup.this,
                                         new PlaceRequest( activity.getNameToken() ) );

                    SelectPlacePopup.this.hide();

                }
            } );
            layout.add( button );
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<IPlaceRequest> handler) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }
}
