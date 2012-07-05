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
package org.drools.guvnor.client.editors.enumeditor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.annotations.IsDirty;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnMayClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.shared.AssetService;
import org.drools.guvnor.shared.common.vo.asset.AbstractAsset;
import org.drools.guvnor.shared.common.vo.assets.enums.EnumModel;
import org.drools.guvnor.vfs.Path;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 */
@Dependent
@WorkbenchEditor(fileType = "enumeration")
public class EnumEditorPresenter {

    @Inject
    View                 view;

    @Inject
    Caller<AssetService> assetService;

    @Inject
    private PlaceManager placeManager;

    public interface View
        extends
        IsWidget {

        void setContent(EnumModel content);

        EnumModel getContent();

        void setFocus();

        void setDirty(boolean dirty);

        boolean isDirty();
    }

    @OnStart
    public void onStart(Path path) {
        assetService.call( new RemoteCallback<AbstractAsset>() {
            @Override
            public void callback(AbstractAsset response) {
                view.setContent( (EnumModel) response );
            }
        } ).loadAsset( path,
                       "enumeration" );
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnMayClose
    public boolean onMayClose() {
        return Window.confirm( "Are you sure you want to close?" );
    }

    @OnReveal
    public void onReveal() {
        view.setFocus();
    }

    @OnFocus
    public void onFocus() {
        view.setFocus();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        final String uriPath = placeRequest.getParameter( "path",
                                                          null );
        return "Enumeration Editor [" + uriPath + "]";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
