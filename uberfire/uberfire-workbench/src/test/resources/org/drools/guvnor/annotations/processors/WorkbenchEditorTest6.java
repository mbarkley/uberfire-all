package org.drools.guvnor.annotations.processors;

import org.drools.guvnor.client.annotations.WorkbenchEditor;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;

import com.google.gwt.user.client.ui.SimplePanel;

@WorkbenchEditor(identifier = "test6", fileTypes = "test6")
public class WorkbenchEditorTest6 extends SimplePanel {

    @WorkbenchPartTitle
    public String getTitle() {
        return "title";
    }

}
