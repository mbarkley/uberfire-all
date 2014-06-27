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
package org.uberfire.client.workbench.pmgr.template;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.AbstractPanelManagerImpl;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

@ApplicationScoped
public class TemplatePanelManager extends AbstractPanelManagerImpl {

    @Inject
    private DefaultBeanFactory factory;

    public TemplatePanelManager() {
        super( new SimplePanel(), new FlowPanel(), new FlowPanel() );
    }

    @Override
    protected BeanFactory getBeanFactory(){
        return factory;
    };

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        WorkbenchPanelPresenter targetPanelPresenter = getOrCreateTargetPanelPresenter( targetPanel );

        final WorkbenchPanelPresenter childPanelPresenter = createChildPresenter( childPanel );

        targetPanelPresenter.addPanel( childPanel,
                                       childPanelPresenter.getPanelView(),
                                       position );
        onPanelFocus( childPanel );

        return childPanel;
    }

    @Override
    public boolean removePartForPlace( PlaceRequest toRemove ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    private WorkbenchPanelPresenter createChildPresenter( PanelDefinition childPanel ) {
        final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
        mapPanelDefinitionToPresenter.put( childPanel,
                                           childPanelPresenter );
        return childPanelPresenter;
    }

    private WorkbenchPanelPresenter getOrCreateTargetPanelPresenter( PanelDefinition targetPanel ) {
        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );
        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }
        return targetPanelPresenter;
    }

    @Override
    public void setWorkbenchSize( int width,
                                  int height ) {
        headerPanel.setWidth( width + "px" );
        footerPanel.setWidth( width + "px" );
        perspectiveRootContainer.setWidth( width + "px" );
        int leftoverHeight = height - headerPanel.getOffsetHeight() - footerPanel.getOffsetHeight();
        perspectiveRootContainer.setHeight( leftoverHeight + "px" );
    }

    @Override
    protected void arrangePanelsInDOM() {
        RootLayoutPanel.get().add( headerPanel );
        RootLayoutPanel.get().add( perspectiveRootContainer );
        RootLayoutPanel.get().add( footerPanel );
    }

}
