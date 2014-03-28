package org.uberfire.security.server.mock;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.security.Role;
import org.uberfire.security.SecurityContext;
import org.uberfire.security.Subject;
import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationManager;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationScheme;
import org.uberfire.security.impl.SubjectImpl;
import org.uberfire.security.impl.RoleImpl;

/**
 * Responds to all authentication requests in the affirmitave, returning an IdentityImpl for a user called "test-user"
 * that belongs to the role "test-role".
 * <p>
 * If you use this mock, you don't need to mock the {@link AuthenticationProvider} or {@link AuthenticationScheme};
 * these are only used by UberFire's default AuthenticationManager.
 * 
 * @author jfuerth
 */
public class MockAuthenticationManager implements AuthenticationManager {

    @Override
    public Subject authenticate( SecurityContext context ) throws AuthenticationException {
        List<Role> roles = new ArrayList<Role>();
        roles.add(new RoleImpl( "test-role" ));
        return new SubjectImpl("test-user", roles);
    }

    @Override
    public void logout( SecurityContext context ) throws AuthenticationException {
        throw new UnsupportedOperationException("Not implemented.");
    }

}
