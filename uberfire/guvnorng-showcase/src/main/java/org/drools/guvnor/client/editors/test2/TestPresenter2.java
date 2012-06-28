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

package org.drools.guvnor.client.editors.test2;

import javax.inject.Inject;

import org.drools.guvnor.client.workbench.annotations.OnClose;
import org.drools.guvnor.client.workbench.annotations.OnFocus;
import org.drools.guvnor.client.workbench.annotations.OnLostFocus;
import org.drools.guvnor.client.workbench.annotations.OnMayClose;
import org.drools.guvnor.client.workbench.annotations.OnReveal;
import org.drools.guvnor.client.workbench.annotations.OnStart;
import org.drools.guvnor.client.workbench.annotations.Title;
import org.drools.guvnor.client.workbench.annotations.WorkbenchWidget;
import org.drools.guvnor.vfs.Path;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A stand-alone Presenter annotated to hook into the Workbench
 */
@WorkbenchWidget(nameToken = "Test2", format = "test2")
public class TestPresenter2 {

    public interface View
        extends
        IsWidget {
    }

    @Inject
    public View view;

    public TestPresenter2() {
    }

    @OnStart
    public void onStart(Path path) {
    }

    @OnMayClose
    public boolean mayClose() {
        return true;
    }

    @OnClose
    public void onClose() {
    }

    @OnReveal
    public void onReveal() {
    }

    @OnLostFocus
    public void onLostFocus() {
    }

    @OnFocus
    public void onFocus() {
    }

    @Title
    public String getTitle() {
        return "Test2";
    }

    @org.drools.guvnor.client.workbench.annotations.View
    public IsWidget getView() {
        return view;
    }

    //@Save
    public void doSave() {
    }

    //@IsDirty
    public boolean isDirty() {
        return false;
    }

}