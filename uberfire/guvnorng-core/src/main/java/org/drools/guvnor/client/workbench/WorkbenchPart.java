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
package org.drools.guvnor.client.workbench;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
@Dependent
public class WorkbenchPart extends SimpleLayoutPanel {

    private String      title;
    private ScrollPanel sp = new ScrollPanel();

    public WorkbenchPart() {
        setWidget( sp );
    }

    public void setPartWidget(IsWidget w) {
        sp.setWidget( w );
    }

    public void setPartTitle(final String title) {
        this.title = title;
    }

    public String getPartTitle() {
        return title;
    }

    @Override
    public void onResize() {
        final Widget parent = getParent();
        if ( parent != null ) {
            sp.setPixelSize( parent.getOffsetWidth(),
                             parent.getOffsetHeight() );
        }
        super.onResize();
    }
}
