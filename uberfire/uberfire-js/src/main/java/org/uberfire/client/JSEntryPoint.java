package org.uberfire.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.backend.plugin.RuntimePluginsService;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;

import static com.google.gwt.core.client.ScriptInjector.*;
import static org.jboss.errai.ioc.client.QualifierUtil.*;

@EntryPoint
public class JSEntryPoint {

    @Inject
    private Caller<RuntimePluginsService> runtimePluginsService;

    @PostConstruct
    public void init() {
        publish();
    }

    @AfterInitialization
    public void setup() {
        runtimePluginsService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> response ) {
                for ( final String s : response ) {
                    ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                }
            }
        } ).listPluginsContent();
    }

    public static void registerPlugin( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativePlugin.hasStringProperty( obj, "id" ) && JSNativePlugin.hasTemplate( obj ) ) {
            final IOCBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativePlugin newNativePlugin = beanManager.lookupBean( JSNativePlugin.class ).getInstance();
            newNativePlugin.build( obj );

            final JSWorkbenchScreenActivity activity = new JSWorkbenchScreenActivity( newNativePlugin, beanManager.lookupBean( PlaceManager.class ).getInstance() );

            beanManager.addBean( (Class) Activity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );

            activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

    // Alias registerPlugin with a global JS function.
    private native void publish() /*-{
        $wnd.$registerPlugin = @org.uberfire.client.JSEntryPoint::registerPlugin(Ljava/lang/Object;);
    }-*/;
}
