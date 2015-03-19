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

package org.uberfire.security.impl.authz;

import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.authz.AuthorizationResult.*;

import java.util.Iterator;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.RoleDecisionManager;
import org.uberfire.security.authz.RolesResource;

public class DefaultRoleDecisionManager implements RoleDecisionManager {

    @Override
    public Iterable<AuthorizationResult> decide(final RolesResource resource, final User user) {
        checkNotNull("resource", resource);
        checkNotNull("subject", user);

        return new Iterable<AuthorizationResult>() {
            @Override
            public Iterator<AuthorizationResult> iterator() {
                return new Iterator<AuthorizationResult>() {

                    private final Iterator<Role> iterator = resource.getRoles().iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public AuthorizationResult next() {
                        final Role role = iterator.next();
                        if ( user.getRoles().contains( role ) ) {
                            return ACCESS_GRANTED;
                        }
                        return ACCESS_ABSTAIN;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Remove not supported.");
                    }
                };
            }
        };
    }
}
