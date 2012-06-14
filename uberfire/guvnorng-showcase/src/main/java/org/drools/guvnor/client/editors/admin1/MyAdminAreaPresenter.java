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

package org.drools.guvnor.client.editors.admin1;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.drools.guvnor.client.mvp.ScreenService;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class MyAdminAreaPresenter
    implements
    ScreenService {
    @Inject
    private PlaceManager placeManager;

    public interface View
        extends
        IsWidget {
        void setName(String name);
    }

    @Inject
    public View view;

    public MyAdminAreaPresenter() {
    }

    @Override
    public void onStart() {
        IPlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
        String uuid = placeRequest.getParameter( "uuid",
                                                 null );
        view.setName( "AdminArea" );
    }

    @Override
    public void onClose() {
    }

    @Override
    public boolean mayClose() {
        return true;
    }

    @Override
    public void onReveal() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onHide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void mayOnHide() {
        // TODO Auto-generated method stub

    }

}