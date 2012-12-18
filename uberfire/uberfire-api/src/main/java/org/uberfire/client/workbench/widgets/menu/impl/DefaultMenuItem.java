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
package org.uberfire.client.workbench.widgets.menu.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.client.workbench.widgets.menu.MenuItem;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * Default implementation of MenuItem
 */
public abstract class DefaultMenuItem
        implements
        MenuItem {

    private boolean enabled = true;

    private String[] roles = new String[]{ };

    private List<EnabledStateChangeListener> enabledStateChangeListeners = new ArrayList<EnabledStateChangeListener>();

    protected final String caption;

    public DefaultMenuItem( final String caption ) {
        checkNotNull( "caption",
                      caption );
        this.caption = caption;
    }

    /**
     * @return the caption
     */
    @Override
    public String getCaption() {
        return caption;
    }

    /**
     * @return the enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    @Override
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
        notifyListeners( enabled );
    }

    @Override
    public void setRoles( final String[] roles ) {
        this.roles = roles;
    }

    @Override
    public void addEnabledStateChangeListener( final EnabledStateChangeListener listener ) {
        PortablePreconditions.checkNotNull( "listener",
                                            listener );
        this.enabledStateChangeListeners.add( listener );
    }

    private void notifyListeners( final boolean enabled ) {
        for ( EnabledStateChangeListener listener : this.enabledStateChangeListeners ) {
            listener.enabledStateChanged( enabled );
        }
    }

    @Override
    public Collection<String> getRoles() {
        String[] clone = new String[ roles.length ];
        System.arraycopy( roles,
                          0,
                          clone,
                          0,
                          roles.length );

        return Arrays.asList( clone );
    }

    @Override
    public Collection<String> getTraits() {
        return Collections.emptyList();
    }

}
