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

import java.util.Collections;
import java.util.List;

import org.uberfire.client.workbench.Position;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * 
 */
public abstract class AbstractScreenActivity
    implements
    WorkbenchScreenActivity {

    @Override
    public Position getDefaultPosition() {
        return Position.ROOT;
    }

    @Override
    public boolean onMayStop() {
        return true;
    }

    @Override
    public void onStop() {
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

    @Override
    public void onRevealPlace(AcceptItem acceptPanel) {
        onStart();
        acceptPanel.add( getTitle(),
                         getWidget() );
        onReveal();
    }

    @Override
    public void onStart() {
        //Do nothing.  
    }

    @Override
    public void onReveal() {
        //Do nothing.   
    }

    public abstract String getTitle();

    //TODO {manstis} This can be deleted once the static popup menu is removed
    public abstract String getIdentifier();

    public abstract IsWidget getWidget();

    @Override
    public void onLostFocus() {
        //Do nothing.
    }

    @Override
    public void onFocus() {
        //Do nothing.
    }

    @Override
    public List<MenuItem> getMenuItems() {
        final List<MenuItem> items = Collections.emptyList();
        return items;
    }

}
