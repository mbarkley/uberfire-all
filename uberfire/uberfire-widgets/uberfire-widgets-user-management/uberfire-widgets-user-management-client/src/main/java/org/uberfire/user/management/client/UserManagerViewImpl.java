/*
 * Copyright 2013 JBoss Inc
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
package org.uberfire.user.management.client;

import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.uberfire.client.common.ButtonCell;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.tables.ResizableHeader;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.user.management.client.resources.i18n.UserManagementConstants;
import org.uberfire.user.management.client.utils.UserManagementUtils;
import org.uberfire.user.management.model.UserInformation;
import org.uberfire.user.management.model.UserManagerContent;

/**
 * Default implementation of the View widget for the User Management Editor
 */
public class UserManagerViewImpl extends Composite implements UserManagementView {

    interface UserManagerWidgetBinder
            extends
            UiBinder<Widget, UserManagerViewImpl> {

    }

    @UiField(provided = true)
    CellTable<UserInformation> table = new CellTable<UserInformation>();
    ListDataProvider<UserInformation> dataProvider = new ListDataProvider<UserInformation>();

    @UiField
    FluidContainer container;

    @UiField
    Button addUserButton;

    protected boolean isReadOnly = false;

    protected ButtonCell editUserRolesButton = new ButtonCell( ButtonSize.SMALL );
    protected Column<UserInformation, String> editUserRolesColumn = new Column<UserInformation, String>( editUserRolesButton ) {
        @Override
        public String getValue( final UserInformation userInformation ) {
            return UserManagementConstants.INSTANCE.editRoles();
        }
    };

    protected ButtonCell editUserPasswordButton = new ButtonCell( ButtonSize.SMALL );
    protected Column<UserInformation, String> editUserPasswordColumn = new Column<UserInformation, String>( editUserPasswordButton ) {
        @Override
        public String getValue( final UserInformation userInformation ) {
            return UserManagementConstants.INSTANCE.editPassword();
        }
    };

    protected ButtonCell deleteUserButton = new ButtonCell( ButtonSize.SMALL );
    protected Column<UserInformation, String> deleteUserColumn = new Column<UserInformation, String>( deleteUserButton ) {
        @Override
        public String getValue( final UserInformation userInformation ) {
            return UserManagementConstants.INSTANCE.remove();
        }
    };

    protected UserManagementPresenter presenter;

    private static UserManagerWidgetBinder uiBinder = GWT.create( UserManagerWidgetBinder.class );

    /**
     * Setup widgets in view
     */
    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        //Setup table
        table.setEmptyTableWidget( new Label( UserManagementConstants.INSTANCE.noUsersDefined() ) );

        final TextColumn<UserInformation> userNameColumn = new TextColumn<UserInformation>() {

            @Override
            public String getValue( final UserInformation userInformation ) {
                return userInformation.getUserName();
            }
        };

        final TextColumn<UserInformation> userRolesColumn = new TextColumn<UserInformation>() {

            @Override
            public String getValue( final UserInformation userInformation ) {
                return UserManagementUtils.convertUserRoles( userInformation.getUserRoles() );
            }

        };

        editUserRolesButton.setType( ButtonType.DEFAULT );
        editUserRolesButton.setIcon( IconType.EDIT );
        editUserRolesColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                presenter.editUserRoles( userInformation );
            }
        } );

        editUserPasswordButton.setType( ButtonType.DEFAULT );
        editUserPasswordButton.setIcon( IconType.EDIT );
        editUserPasswordColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                presenter.editUserPassword( userInformation );
            }
        } );

        deleteUserButton.setType( ButtonType.DANGER );
        deleteUserButton.setIcon( IconType.MINUS_SIGN );
        deleteUserColumn.setFieldUpdater( new FieldUpdater<UserInformation, String>() {
            public void update( final int index,
                                final UserInformation userInformation,
                                final String value ) {
                if ( isReadOnly ) {
                    return;
                }
                if ( Window.confirm( UserManagementConstants.INSTANCE.promptForRemovalOfUser0( userInformation.getUserName() ) ) ) {
                    presenter.deleteUser( userInformation );
                }
            }
        } );

        table.addColumn( userNameColumn,
                         new ResizableHeader( UserManagementConstants.INSTANCE.userName(),
                                              table,
                                              userNameColumn ) );
        table.addColumn( userRolesColumn,
                         new ResizableHeader( UserManagementConstants.INSTANCE.userRoles(),
                                              table,
                                              userRolesColumn ) );
        table.addColumn( editUserPasswordColumn,
                         UserManagementConstants.INSTANCE.editPassword() );
        table.addColumn( editUserRolesColumn,
                         UserManagementConstants.INSTANCE.editRoles() );
        table.addColumn( deleteUserColumn,
                         UserManagementConstants.INSTANCE.remove() );

        //Default to disabled, until we know what features are supported
        addUserButton.setEnabled( false );
        editUserRolesButton.setEnabled( false );
        editUserPasswordButton.setEnabled( false );
        deleteUserButton.setEnabled( false );
    }

    /**
     * Inject Presenter into View to support MVP pattern
     * @param presenter
     * @see UberView#init(Object)
     */
    @Override
    public void init( final UserManagementPresenter presenter ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter",
                                                             presenter );
    }

    /**
     * @param content
     * @param isReadOnly
     * @see UserManagementView#setContent(UserManagerContent, boolean)
     */
    @Override
    public void setContent( final UserManagerContent content,
                            final boolean isReadOnly ) {
        this.isReadOnly = isReadOnly;
        this.dataProvider.setList( content.getUserInformation() );
        this.dataProvider.addDataDisplay( table );
        final boolean isAddUserSupported = content.getCapabilities().isAddUserSupported();
        final boolean isUpdateUserRolesSupported = content.getCapabilities().isUpdateUserRolesSupported();
        final boolean isUpdateUserPasswordSupported = content.getCapabilities().isUpdateUserPasswordSupported();
        final boolean isDeleteUserSupported = content.getCapabilities().isDeleteUserSupported();
        addUserButton.setEnabled( !isReadOnly && isAddUserSupported );
        editUserRolesButton.setEnabled( !isReadOnly && isUpdateUserRolesSupported );
        editUserPasswordButton.setEnabled( !isReadOnly && isUpdateUserPasswordSupported );
        deleteUserButton.setEnabled( !isReadOnly && isDeleteUserSupported );
    }

    /**
     * @param userInformation Basic user information of new user. Cannot be null.
     * @see UserManagementView#addUser(UserInformation)
     */
    @Override
    public void addUser( final UserInformation userInformation ) {
        this.dataProvider.getList().add( userInformation );
    }

    /**
     * @param oldUserInformation Original user information. Cannot be null.
     * @param newUserInformation Updated user information. Cannot be null.
     * @see UserManagementView#updateUser(UserInformation, UserInformation)
     */
    @Override
    public void updateUser( final UserInformation oldUserInformation,
                            final UserInformation newUserInformation ) {
        final int idx = this.dataProvider.getList().indexOf( oldUserInformation );
        if ( idx < 0 ) {
            return;
        }
        this.dataProvider.getList().set( idx,
                                         newUserInformation );
    }

    /**
     * @param userInformation Basic user information or user to be removed. Cannot be null.
     * @see UserManagementView#deleteUser(UserInformation)
     */
    @Override
    public void deleteUser( final UserInformation userInformation ) {
        this.dataProvider.getList().remove( userInformation );
    }

    /**
     * Click Handler for "Add" (User) button
     * @param event
     * @see ClickHandler#onClick(ClickEvent)
     */
    @UiHandler(value = "addUserButton")
    public void onClickAddUserButton( final ClickEvent event ) {
        presenter.addUser();
    }

}
