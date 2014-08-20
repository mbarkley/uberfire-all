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
package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.mvp.Command;

/**
 * A Workbench panel that can contain WorkbenchParts.
 */
@Dependent
@Named("MultiListWorkbenchPanelView")
public class MultiListWorkbenchPanelView
extends AbstractMultiPartWorkbenchPanelView<MultiListWorkbenchPanelPresenter> {

    @Inject
    protected ListBarWidget listBar;

    @Override
    protected MultiPartWidget setupWidget() {
        if ( contextWidget != null ) {
            listBar.setExpanderCommand( new Command() {
                @Override
                public void execute() {
                    contextWidget.toogleDisplay();
                }
            } );
        }
        addOnFocusHandler( listBar );
        addSelectionHandler( listBar );
        return listBar;
    }

    @Override
    public void onResize() {
        int width = getOffsetWidth();
        int height = getOffsetHeight();
        listBar.setPixelSize( width, height );
        listBar.onResize();
        super.onResize();
    }
}
