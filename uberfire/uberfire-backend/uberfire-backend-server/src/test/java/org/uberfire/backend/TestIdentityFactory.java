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

package org.uberfire.backend;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;

@Singleton
@Alternative
public class TestIdentityFactory {

    private User user;

    @PostConstruct
    public void onStartup() {
        user = new User() {

            @Override
            public String getIdentifier() {
                return "testUser";
            }

            @Override
            public Set<Role> getRoles() {
                return Collections.emptySet();
            }

            @Override
            public boolean hasAllRoles( String... roleNames ) {
                return false;
            }

            @Override
            public boolean hasAnyRoles( String... roleNames ) {
                return false;
            }

            @Override
            public Map<String, String> getProperties() {
                return Collections.emptyMap();
            }

            @Override
            public void setProperty( String name,
                                     String value ) {
            }

            @Override
            public void removeProperty( String name ) {
            }

            @Override
            public String getProperty( String name ) {
                return null;
            }

        };
    }

    @Produces
    @Alternative
    public User getIdentity() {
        return user;
    }

}
