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

package org.drools.guvnor.client.perspective.workspace.explorer;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.content.AdminPlace;
import org.drools.guvnor.client.content.AdminPlace2;
import org.drools.guvnor.client.content.editor.TextEditorPlace;
import org.drools.guvnor.client.i18n.Constants;
import org.drools.guvnor.client.perspective.monitoring.MonitoringPerspectivePlace;
import org.drools.guvnor.client.resources.Images;

@Dependent
public class AdminTree extends AbstractTree {

    @Inject private PlaceController placeController;

    public static final String ADMIN_ID = "Admin";
    public static final String ADMIN2_ID = "Admin2";
    public static final String CHANGE_PERSPECTIVE_ID = "OtherPerspective";
    public static final String FILE1_ID = "MyHack1.hack";
    public static final String FILE2_ID = "MyHack2.hack";

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    @PostConstruct
    public void init() {
        super.init();
        this.name = constants.admin();
        this.image = images.config();

        mainTree.setAnimationEnabled(true);
        setupTree(mainTree, itemWidgets);
        mainTree.addSelectionHandler(this);
    }

    @Override
    protected Tree createTree() {
        return new Tree();
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();
        setupTree(mainTree, itemWidgets);
    }

    public void setupTree(final Tree tree, final Map<TreeItem, String> itemWidgets) {
        final TreeItem admin = tree.addItem(Util.getHeader(images.analyze(), constants.admin()));
        itemWidgets.put(admin, ADMIN_ID);

        final TreeItem admin2 = tree.addItem(Util.getHeader(images.information(), "admin2"));
        itemWidgets.put(admin2, ADMIN2_ID);

        final TreeItem file1 = tree.addItem(Util.getHeader(images.edit(), FILE1_ID));
        itemWidgets.put(file1, FILE1_ID);

        final TreeItem file2 = tree.addItem(Util.getHeader(images.edit(), FILE2_ID));
        itemWidgets.put(file2, FILE2_ID);

        final TreeItem changePerspective = tree.addItem(Util.getHeader(images.newItem(), "monitoring"));
        itemWidgets.put(changePerspective, CHANGE_PERSPECTIVE_ID);
    }

    public void onSelection(SelectionEvent<TreeItem> event) {
        final TreeItem item = event.getSelectedItem();
        final String widgetID = itemWidgets.get(item);

        if (widgetID != null) {
            final Place place;
            if (widgetID.equals(ADMIN_ID)) {
                place = new AdminPlace("helloWorld" + "|" + constants.helloWorld());
            } else if (widgetID.equals(ADMIN2_ID)) {
                place = new AdminPlace2("helloWorld" + "|" + constants.helloWorld() + " 2");
            } else if (widgetID.equals(FILE1_ID)) {
                place = new TextEditorPlace("hackFile" + "|" + FILE1_ID);
            } else if (widgetID.equals(FILE2_ID)) {
                place = new TextEditorPlace("hackFile" + "|" + FILE2_ID);
            } else if (widgetID.equals(CHANGE_PERSPECTIVE_ID)) {
                place = new MonitoringPerspectivePlace();
            } else {
                place = null;
            }
            placeController.goTo(place);
        }
    }
}
