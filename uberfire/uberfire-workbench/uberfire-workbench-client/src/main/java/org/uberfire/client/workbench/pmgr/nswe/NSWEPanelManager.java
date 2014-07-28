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
package org.uberfire.client.workbench.pmgr.nswe;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.AbstractPanelManagerImpl;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
public class NSWEPanelManager extends AbstractPanelManagerImpl {

    @Inject
    private BeanFactory factory;

    @Override
    protected BeanFactory getBeanFactory(){
        return factory;
    };

    @Override
    public PanelDefinition addWorkbenchPanel( final PanelDefinition targetPanel,
                                              final PanelDefinition childPanel,
                                              final Position position ) {

        System.out.println("    Adding panel " + childPanel.getPanelType() + "@" + System.identityHashCode( childPanel ) );
        WorkbenchPanelPresenter targetPanelPresenter = mapPanelDefinitionToPresenter.get( targetPanel );

        if ( targetPanelPresenter == null ) {
            targetPanelPresenter = factory.newWorkbenchPanel( targetPanel );
            mapPanelDefinitionToPresenter.put( targetPanel,
                                               targetPanelPresenter );
        }

        if ( childPanel.getPanelType().equals( PanelDefinition.PARENT_CHOOSES_TYPE ) ) {
            childPanel.setPanelType( targetPanelPresenter.getDefaultChildType() );
        }

        PanelDefinition newPanel;
        if ( position == CompassPosition.ROOT ) {
            // TODO not sure if this is needed/used anymore
            newPanel = rootPanelDef;
        } else if ( position == CompassPosition.SELF ) {
            // TODO not sure if this is needed/used anymore
            newPanel = targetPanelPresenter.getDefinition();
        } else {
            final WorkbenchPanelPresenter childPanelPresenter = factory.newWorkbenchPanel( childPanel );
            mapPanelDefinitionToPresenter.put( childPanel,
                                               childPanelPresenter );

            // TODO (hbraun): why no remove callback before the addPanel invocation?
            targetPanelPresenter.addPanel( childPanel,
                                           childPanelPresenter.getPanelView(),
                                           position );
            newPanel = childPanel;
        }

        onPanelFocus( newPanel );

        System.out.println("  ->Added  panel " + childPanel.getPanelType() + "@" + System.identityHashCode( childPanel ) );
        dumpKnownPanels();

        return newPanel;
    }

}
