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

package org.uberfire.client.editors.fileexplorer;

import javax.annotation.PostConstruct;

import org.uberfire.client.common.Util;
import org.uberfire.client.resources.ComponentCoreImages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class FileExplorerView extends Composite
    implements
    FileExplorerPresenter.View {

    final Tree                    tree   = new Tree();
    TreeItem                      rootTreeItem;
    private static ComponentCoreImages images = GWT.create( ComponentCoreImages.class );

    @PostConstruct
    public void init() {
        rootTreeItem = tree.addItem( Util.getHeader( images.packageIcon(),
                                                     "Repositories" ) );
        rootTreeItem.setState(true);
        initWidget( tree );
    }

    @Override
    public TreeItem getRootItem() {
        return rootTreeItem;
    }

    @Override
    public Tree getTree() {
        return tree;
    }

    @Override
    public void setFocus() {
        tree.setFocus( true );
    }

}