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

package org.uberfire.client.mvp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.mvp.PathPlaceRequest;
import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;
import org.uberfire.shared.mvp.PlaceRequest;

import static com.google.gwt.regexp.shared.RegExp.*;
import static java.util.Collections.*;

@ApplicationScoped
public class ActivityManagerImpl
        implements
        ActivityManager {

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private RuntimeAuthorizationManager authzManager;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private Identity identity;

    @Override
    public <T extends Activity> Set<T> getActivities( final Class<T> clazz ) {
        return secure( iocManager.lookupBeans( clazz ) );
    }

    @Override
    public Set<Activity> getActivities( final PlaceRequest placeRequest ) {

        final Collection<IOCBeanDef<Activity>> beans;
        if ( placeRequest instanceof PathPlaceRequest ) {
            beans = resolveByPath( (PathPlaceRequest) placeRequest );
        } else {
            beans = resolveById( placeRequest.getIdentifier() );
        }

        return secure( beans );
    }

    public <T extends Activity> Set<T> secure( final Collection<IOCBeanDef<T>> activityBeans ) {
        final Set<T> activities = new HashSet<T>( activityBeans.size() );

        for ( final IOCBeanDef<T> activityBean : activityBeans ) {
            final T instance = activityBean.getInstance();
            if ( authzManager.authorize( instance, identity ) ) {
                activities.add( instance );
            } else {
                //If user does not have permission destroy bean to avoid memory leak
                if ( activityBean.getScope().equals( Dependent.class ) ) {
                    iocManager.destroyBean( instance );
                }
            }
        }

        return activities;
    }

    /**
     * Get a set of Bean definitions that can handle the @Identifier
     * @param identifier
     * @return
     */
    private Collection<IOCBeanDef<Activity>> resolveById( final String identifier ) {
        if ( identifier == null ) {
            return emptyList();
        }

        return new ArrayList<IOCBeanDef<Activity>>( 1 ) {{
            add( activityBeansCache.getActivityById( identifier ) );
        }};
    }

    private Set<IOCBeanDef<Activity>> resolveByPath( final PathPlaceRequest place ) {
        if ( place == null ) {
            return Collections.emptySet();
        }
        final IOCBeanDef<Activity> result = activityBeansCache.getActivityById( place.getIdentifier() );

        if ( result != null ) {
            return new HashSet<IOCBeanDef<Activity>>( 1 ) {{
                add( result );
            }};
        }

        final Set<IOCBeanDef<Activity>> matchingActivityBeans = new HashSet<IOCBeanDef<Activity>>();
        for ( final String pattern : activityBeansCache.getPatterns() ) {
            if ( compile( pattern ).test( place.getPath().getFileName() ) ) {
                matchingActivityBeans.add( activityBeansCache.cachedActivitiesByPattern( pattern ) );
                break;
            }

        }

        if ( matchingActivityBeans.isEmpty() ) {
            matchingActivityBeans.add( activityBeansCache.getDefaultActivity() );
        }

        return unmodifiableSet( matchingActivityBeans );
    }

}
