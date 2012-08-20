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
package org.uberfire.client.workbench.perspectives;

import java.util.HashSet;
import java.util.Set;

/**
 * Meta-data defining a Perspective. A Perspective is a set of WorkbenchPanels
 * and WorkbenchParts arranged within the Workbench. Each WorkbenchPart
 * containing a PlaceRequest. Perspectives can be persisted.
 */
public class PerspectiveDefinition {

    private String name;

    private Set<PerspectivePartDefinition> parts = new HashSet<PerspectivePartDefinition>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void addPart(final PerspectivePartDefinition part) {
        parts.add( part );
    }

    public Set<PerspectivePartDefinition> getParts() {
        return this.parts;
    }

}
