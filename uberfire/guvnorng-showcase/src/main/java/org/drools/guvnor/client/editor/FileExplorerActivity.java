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

package org.drools.guvnor.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;


import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.ScreenService;
import org.drools.guvnor.client.workbench.Position;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

@Dependent
@NameToken("File Explorer")
public class FileExplorerActivity implements Activity {

    private FileExplorerPresenter presenter;
    @Inject
    private IOCBeanManager manager;
    
    public FileExplorerActivity() {
    }

    @Override
    public String getNameToken() {
        return "File Explorer";
    }

    @Override
    public void start() {
    }

    @Override
    public boolean mayStop() {
        return true;
    }

    @Override
    public void onStop() {
        //TODO: -Rikkola-
    }

    @Override
    public Position getPreferredPosition() {
        return Position.WEST;
    }
    
    public void revealPlace(AcceptItem acceptPanel) {
        if(presenter == null) {
            presenter = manager.lookupBean(FileExplorerPresenter.class).getInstance();        
            if(presenter instanceof ScreenService) {
                ((ScreenService) presenter).onStart();
            }
            //TODO: Get tab title (or an closable title bar widget).        
            acceptPanel.add("File Explorer", presenter.view);   
        }
        
        if(presenter instanceof ScreenService) {
            ((ScreenService) presenter).onReveal();
        }  
    }
/*    
    public void revealPlace1(AcceptItem acceptPanel) {
        final Tree tree = new Tree();
        final TreeItem root = tree.addItem(Util.getHeader(images.openedFolder(), "Home"));

        vfsService.call(new RemoteCallback<DirectoryStream<ExtendedPath>>() {
            @Override
            public void callback(DirectoryStream<ExtendedPath> response) {
                for (final ExtendedPath path : response) {
                    final TreeItem item;
                    if (path.isDirectory()) {
                        item = root.addItem(Util.getHeader(images.openedFolder(), path.getFileName().toString()));
                        item.addItem(LAZY_LOAD);
                    } else {
                        item = root.addItem(Util.getHeader(images.file(), path.getFileName().toString()));
                    }
                    item.setUserObject(path);
                }
            }
        }).newDirectoryStream();

        tree.addOpenHandler(new OpenHandler<TreeItem>() {
            @Override public void onOpen(final OpenEvent<TreeItem> event) {
                if (needsLoading(event.getTarget())) {
                    vfsService.call(new RemoteCallback<DirectoryStream<ExtendedPath>>() {
                        @Override
                        public void callback(DirectoryStream<ExtendedPath> response) {
                            event.getTarget().getChild(0).remove();
                            for (final ExtendedPath path : response) {
                                final TreeItem item;
                                if (path.isDirectory()) {
                                    item = event.getTarget().addItem(Util.getHeader(images.openedFolder(), path.getFileName().toString()));
                                    item.addItem(LAZY_LOAD);
                                } else {
                                    item = event.getTarget().addItem(Util.getHeader(images.file(), path.getFileName().toString()));
                                }
                                item.setUserObject(path);
                            }
                        }
                    }).newDirectoryStream(event.getTarget().getUserObject().toString());
                }

            }
        });

        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {
            @Override
            public void onSelection(SelectionEvent<TreeItem> event) {
                final ExtendedPath path = (ExtendedPath) event.getSelectedItem().getUserObject();
                if (path.isRegularFile()) {
                    PlaceRequest placeRequest = new PlaceRequest("TextEditor");
                    placeRequest.parameter("path", path.toUriAsString());
                    placeManager.goTo(placeRequest);
                }
            }
        });

        acceptPanel.add("File Explorer", tree);
    }

    private boolean needsLoading(TreeItem item) {
        return item.getChildCount() == 1
                && LAZY_LOAD.equals(item.getChild(0).getText());
    }*/

}
