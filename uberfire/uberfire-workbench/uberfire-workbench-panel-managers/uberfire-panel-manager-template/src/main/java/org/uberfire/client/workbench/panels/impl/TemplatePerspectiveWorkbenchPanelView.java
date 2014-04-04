package org.uberfire.client.workbench.panels.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;

@Dependent
@Named("TemplatePerspectiveWorkbenchPanelView")
public class TemplatePerspectiveWorkbenchPanelView extends AbstractTemplateWorkbenchPanelView<TemplatePerspectiveWorkbenchPanelPresenter> {


    @Override
    public Widget asWidget() {
        TemplatePanelDefinitionImpl definition = (TemplatePanelDefinitionImpl) getPresenter().getDefinition();
        return definition.getRealPresenterWidget();
    }

}
