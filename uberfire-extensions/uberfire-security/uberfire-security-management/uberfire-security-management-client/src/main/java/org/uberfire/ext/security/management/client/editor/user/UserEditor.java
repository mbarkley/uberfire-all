/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.editor.user;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.ext.security.management.client.editor.Editor;

/**
 * <p>A user editor.</p>
 * Note:Add roles sub-editor when they're supported in the future.
 * 
 * @since 0.8.0
 */
public interface UserEditor extends Editor<User, User> {

    /**
     * The leaf value user's identifier.
     */
    String identifier();
    
    /**
     * The editor for the user's attributes.
     */
    UserAttributesEditor attributesEditor();

    /**
     * The explorer for the user's groups. It's considered an editor as it allows removing assigned groups from the user instance. 
     */
    UserAssignedGroupsExplorer groupsExplorer();

    /**
     * The explorer for the user's roles. It's considered an editor as it allows removing assigned roles from the user instance. 
     */
    UserAssignedRolesExplorer rolesExplorer();

}
