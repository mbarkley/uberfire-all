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

package org.drools.guvnor.shared;


import com.google.gwt.user.client.rpc.SerializationException;

import org.drools.guvnor.vfs.Path;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface SuggestionCompletionEngineService {

    /**
     * Loads up the SuggestionCompletionEngine for the given package. As this
     * doesn't change that often, its safe to cache. However, if a change is
     * made to a package, should blow away the cache.
     */
    //public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializationException;

}
