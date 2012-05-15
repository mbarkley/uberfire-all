/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.perspective.workspace;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.perspective.workspace.explorer.AdminTree;
import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.client.resources.Images;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

/**
 * Navigation panel for the west area.
 */
@Dependent
public class NavigationPanel extends Composite {

    private Constants constants = GWT.create(Constants.class);
    private Images images = GWT.create(Images.class);

    @Inject private IOCBeanManager manager;

    private StackLayoutPanel layout = new StackLayoutPanel(Unit.EM);

    @PostConstruct
    public void init() {
        initWidget(layout);
        addAdminPanel();
        addAdminPanel();
    }

    private void addAdminPanel() {
        final DockLayoutPanel browseDockLayoutPanel = new DockLayoutPanel(Unit.EM);

        final AdminTree tree = manager.lookupBean(AdminTree.class).getInstance();
        tree.setStyleName("lhs-Tree");
        final ScrollPanel treePanel = new ScrollPanel(tree);
        browseDockLayoutPanel.add(treePanel);

        layout.add(browseDockLayoutPanel, Util.getHeaderHTML(images.config(), constants.admin()), 2);
    }

}
