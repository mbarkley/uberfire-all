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
package org.uberfire.client.workbench.model.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;

/**
 * Default implementation of PerspectiveDefinition
 */
@Portable
public class PerspectiveDefinitionImpl
    implements
    PerspectiveDefinition {

    private String          name;

    private boolean         isTransient = false;

    private PanelDefinition root        = new PanelDefinitionImpl( true );

    @Override
    public boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public PanelDefinition getRoot() {
        return root;
    }

}
