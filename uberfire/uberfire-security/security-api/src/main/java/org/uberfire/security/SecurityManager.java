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

package org.uberfire.security;

import org.uberfire.security.auth.AuthenticationException;

public interface SecurityManager {

    SecurityContext newSecurityContext(final Object... params);

    void logout(SecurityContext context);

    Subject authenticate(final SecurityContext context) throws AuthenticationException;

    boolean authorize(final SecurityContext context);

    void dispose();

    void start();
}
