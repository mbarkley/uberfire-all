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
package org.drools.guvnor.client.editors.repositorieseditor;

import javax.enterprise.context.ApplicationScoped;

import org.drools.guvnor.client.mvp.IPlaceRequestFactory;
import org.drools.guvnor.vfs.Path;

/**
 * 
 */
@ApplicationScoped
public class RepositoriesEditorPlaceFactory
    implements
    IPlaceRequestFactory<RepositoriesEditorPlace> {

    private static final String           FACTORY_NAME = "RepositoriesEditor";

    private static final RepositoriesEditorPlace PLACE        = new RepositoriesEditorPlace();

    @Override
    public String getFactoryName() {
        return FACTORY_NAME;
    }

    @Override
    public RepositoriesEditorPlace makePlace(Path path) {
        return PLACE;
    }

    @Override
    public RepositoriesEditorPlace makePlace() {
        return PLACE;
    }

}
