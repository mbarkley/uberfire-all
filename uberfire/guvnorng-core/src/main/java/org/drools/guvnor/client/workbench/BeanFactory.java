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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.workbench.widgets.dnd.CompassDropController;
import org.drools.guvnor.client.workbench.widgets.panels.HorizontalSplitterPanel;
import org.drools.guvnor.client.workbench.widgets.panels.VerticalSplitterPanel;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.user.client.ui.Widget;

/**
 * A convenience class to create new instances of managed beans.
 */
@ApplicationScoped
public class BeanFactory {

    @Inject
    private IOCBeanManager iocManager;

    public WorkbenchPanel newWorkbenchPanel() {
        final WorkbenchPanel panel = (WorkbenchPanel) iocManager.lookupBean( WorkbenchPanel.class ).getInstance();
        return panel;
    }

    public WorkbenchPanel newWorkbenchPanel(final WorkbenchPart part) {
        final WorkbenchPanel panel = (WorkbenchPanel) iocManager.lookupBean( WorkbenchPanel.class ).getInstance();
        panel.addTab( part );
        return panel;
    }

    public WorkbenchPart newWorkbenchPart(final Widget w,
                                          final String title) {
        final WorkbenchPart part = (WorkbenchPart) iocManager.lookupBean( WorkbenchPart.class ).getInstance();
        part.setPartWidget( w );
        part.setPartTitle( title );
        return part;
    }

    public HorizontalSplitterPanel newHorizontalSplitterPanel(final WorkbenchPanel eastPanel,
                                                              final WorkbenchPanel westPanel,
                                                              final Position position) {
        final HorizontalSplitterPanel hsp = (HorizontalSplitterPanel) iocManager.lookupBean( HorizontalSplitterPanel.class ).getInstance();
        hsp.setup( eastPanel,
                   westPanel,
                   position );
        return hsp;
    }

    public VerticalSplitterPanel newVerticalSplitterPanel(final WorkbenchPanel northPanel,
                                                          final WorkbenchPanel southPanel,
                                                          final Position position) {
        final VerticalSplitterPanel vsp = (VerticalSplitterPanel) iocManager.lookupBean( VerticalSplitterPanel.class ).getInstance();
        vsp.setup( northPanel,
                   southPanel,
                   position );
        return vsp;
    }

    public CompassDropController newDropController(final WorkbenchPanel panel) {
        final CompassDropController dropController = (CompassDropController) iocManager.lookupBean( CompassDropController.class ).getInstance();
        dropController.setup( panel );
        return dropController;
    }

    //TODO {manstis} We don't release any objects we create
    public void release(final Object o) {
        iocManager.destroyBean( o );
    }

}
