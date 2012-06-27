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

package org.drools.guvnor.client.editors.repositorieseditor;

import javax.enterprise.context.Dependent;

import org.drools.guvnor.client.mvp.PlaceRequest;

@Dependent
public class RepositoriesEditorPlace extends PlaceRequest {

    private static final String PLACE_NAME = "RepositoriesEditor";

    public RepositoriesEditorPlace() {
        super( PLACE_NAME );
    }

}
