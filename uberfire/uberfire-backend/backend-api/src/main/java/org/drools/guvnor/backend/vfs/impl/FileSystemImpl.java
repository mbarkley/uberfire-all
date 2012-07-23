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

package org.drools.guvnor.backend.vfs.impl;

import java.util.List;

import org.drools.guvnor.backend.vfs.FileSystem;
import org.drools.guvnor.backend.vfs.Path;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class FileSystemImpl implements FileSystem {

    private List<Path> rootDirectories = null;

    public FileSystemImpl() {
    }

    public FileSystemImpl(List<Path> rootDirectories) {
        this.rootDirectories = rootDirectories;
    }

    @Override
    public List<Path> getRootDirectories() {
        return rootDirectories;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (rootDirectories != null) {
            for (final Path rootDirectory : rootDirectories) {
                sb.append(rootDirectory.toString());
            }
        }
        return sb.toString();
    }
}
