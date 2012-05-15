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

package org.drools.guvnor.client.content;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.guvnor.client.ui.part.WorkbenchPart;

@Dependent
public class AdminArea2Presenter extends WorkbenchPart {

    public interface View extends IsWidget {

        void setName(String name);
    }

    @Inject private View view;

    @Override public void createPartControl(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override public String getName() {
        return "org.drools.guvnor.client.content.AdminArea2";
    }

    @Override public void dispose() {
    }

    @Override public void setFocus() {
    }

}