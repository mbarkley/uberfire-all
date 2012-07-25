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
package org.uberfire.client.moshpit;

import javax.inject.Inject;

import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.WorkbenchPart;
import org.uberfire.client.workbench.widgets.panels.PanelManager;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Popup to select a location to add a new widget
 */
public class PositionSelectorPopup extends PopupPanel {

    private ListBox      positionChoices = new ListBox();

    private int          widgetCounter   = 1;

    @Inject
    private PanelManager panelManager;

    public PositionSelectorPopup() {
        initChoices();
        add( positionChoices );
    }

    private void initChoices() {
        for ( Position p : Position.values() ) {
            positionChoices.addItem( p.toString(),
                                     p.name() );
        }
        positionChoices.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                final int selectedIndex = positionChoices.getSelectedIndex();
                if ( selectedIndex == -1 ) {
                    return;
                }
                final String selection = positionChoices.getValue( selectedIndex );
                Position position = Position.valueOf( selection );
                hide();

                final String title = position.toString() + " [" + (widgetCounter++) + "]";
                WorkbenchPart part = new WorkbenchPart( new DebugLabel(),
                                                        title );
                panelManager.addWorkbenchPanel( part,
                                                position );
            }

        } );
    }

    @Override
    public void show() {
        positionChoices.setSelectedIndex( 0 );
        super.show();
    }

}
