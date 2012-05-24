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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.mvp.AcceptItem;
import org.drools.guvnor.client.mvp.Activity;
import org.drools.guvnor.client.workbench.Position;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MonitoringPerspectivePresenter implements Activity {

    @Override
    public String getNameToken() {
        return "monitoring_perspective";
    }

    @Override
    public void start(AcceptItem tabbedPanel) {
        tabbedPanel.add("monitoring_perspective", view);
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
        return Position.SELF;
    }

    public interface MyView extends IsWidget {

        void setUserName(String userName);
    }

    @Inject
    MyView view;

    public void start(final AcceptsOneWidget acceptsOneWidget, final EventBus eventBus) {
        acceptsOneWidget.setWidget(view);
    }

    @Override
    public void revealPlace(AcceptItem acceptPanel) {
        acceptPanel.add("monitoring_perspective", view);        
    }
}
