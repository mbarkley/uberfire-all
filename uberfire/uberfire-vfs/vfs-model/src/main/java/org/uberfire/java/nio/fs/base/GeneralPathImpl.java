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

package org.uberfire.java.nio.fs.base;

import java.io.File;
import java.util.regex.Pattern;

import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.util.Preconditions.*;

public class GeneralPathImpl extends AbstractPath {

    private GeneralPathImpl(final FileSystem fs, final File file) {
        super(fs, file);
    }

    private GeneralPathImpl(final FileSystem fs, final String path, boolean isRoot, boolean isRealPath, boolean isNormalized) {
        super(fs, path, isRoot, isRealPath, isNormalized);
    }

    public static GeneralPathImpl newFromFile(final FileSystem fs, final File file) {
        checkNotNull("fs", fs);
        checkNotNull("file", file);

        return new GeneralPathImpl(fs, file);
    }

    public static GeneralPathImpl create(final FileSystem fs, final String path, boolean isRealPath) {
        return create(fs, path, isRealPath, false);
    }

    public static GeneralPathImpl create(final FileSystem fs, final String path, boolean isRealPath, boolean isNormalized) {
        checkNotNull("fs", fs);
        checkNotNull("path", path);

        return new GeneralPathImpl(fs, path, false, isRealPath, isNormalized);
    }

    @Override Path newRoot(FileSystem fs, String substring, boolean realPath) {
        return new GeneralPathImpl(fs, substring, true, realPath, true);
    }

    @Override Path newPath(final FileSystem fs, final String substring, final boolean isRealPath, final boolean isNormalized) {
        return new GeneralPathImpl(fs, substring, false, isRealPath, isNormalized);
    }
}
