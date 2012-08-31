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
package org.uberfire.client.workbench.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.client.workbench.Position;

/**
 * 
 */
@Portable
public class PanelDefinition {

    private List<PartDefinition>                 parts    = new ArrayList<PartDefinition>();
    private Map<Position, List<PanelDefinition>> children = new HashMap<Position, List<PanelDefinition>>();

    public PanelDefinition() {
        children.put( Position.NORTH,
                      new ArrayList<PanelDefinition>() );
        children.put( Position.SOUTH,
                      new ArrayList<PanelDefinition>() );
        children.put( Position.EAST,
                      new ArrayList<PanelDefinition>() );
        children.put( Position.WEST,
                      new ArrayList<PanelDefinition>() );
    }

    public void addPart(final PartDefinition part) {
        this.parts.add( part );
    }

    public List<PartDefinition> getParts() {
        return parts;
    }

    public Map<Position, List<PanelDefinition>> getChildren() {
        return children;
    }

    public List<PanelDefinition> getChildren(final Position position) {
        return children.get( position );
    }

}
