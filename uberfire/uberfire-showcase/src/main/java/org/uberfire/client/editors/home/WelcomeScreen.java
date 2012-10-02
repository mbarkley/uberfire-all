package org.uberfire.client.editors.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

@Dependent
@WorkbenchScreen(identifier = "welcome")
public class WelcomeScreen
        extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, WelcomeScreen> {
    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Welcome";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }
}
