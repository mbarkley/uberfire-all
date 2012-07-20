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
package org.drools.guvnor.server.impl;

import org.drools.guvnor.server.builder.ClassLoaderBuilder;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.java.nio.file.Path;

public class SuggestionCompletionEngineLoaderInitializer {

    protected SuggestionCompletionEngine loadFor(final Path packageRootDir) {
        SuggestionCompletionEngine result = null;
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            BRMSSuggestionCompletionLoader loader = null;
            ClassLoaderBuilder classLoaderBuilder = new ClassLoaderBuilder(packageRootDir);
            if (classLoaderBuilder.hasJars()) {
                ClassLoader classLoader = classLoaderBuilder.buildClassLoader();

                Thread.currentThread().setContextClassLoader(classLoader);

                loader = new BRMSSuggestionCompletionLoader(classLoader);
            } else {
                loader = new BRMSSuggestionCompletionLoader();
            }

            result = loader.getSuggestionEngine(packageRootDir, "");

        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
        return result;
    }
}
