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
package org.drools.guvnor.client.mvp;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 */
public abstract class AbstractPopupActivity
    implements
    PopupActivity {

    private BaseService presenter;

    @Override
    public void onRevealPlace() {
        if ( presenter == null ) {
            presenter = getPresenter();
            if ( presenter == null ) {
                return;
            }
        }
        presenter.onReveal();
        final PopupPanel popup = getPopupPanel();
        popup.show();
        popup.center();
    }

    public abstract BaseService getPresenter();

    public abstract PopupPanel getPopupPanel();

}
