/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.annotations.processors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.drools.guvnor.annotations.processors.exceptions.GenerationException;
import org.drools.guvnor.client.annotations.DefaultPosition;
import org.drools.guvnor.client.annotations.IsDirty;
import org.drools.guvnor.client.annotations.OnClose;
import org.drools.guvnor.client.annotations.OnFocus;
import org.drools.guvnor.client.annotations.OnLostFocus;
import org.drools.guvnor.client.annotations.OnMayClose;
import org.drools.guvnor.client.annotations.OnReveal;
import org.drools.guvnor.client.annotations.OnSave;
import org.drools.guvnor.client.annotations.OnStart;
import org.drools.guvnor.client.annotations.WorkbenchPartTitle;
import org.drools.guvnor.client.annotations.WorkbenchPartView;

/**
 * Utilities for code generation
 */
public class GeneratorUtils {

    /**
     * Get the method name annotated with {@code @OnStart}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartZeroParameterMethodName(final TypeElement classElement,
                                                           final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnStart.class );
    }

    /**
     * Get the method name annotated with {@code @OnStart}. The method must be
     * public, non-static, have a return-type of void and take one parameter of
     * type {@code org.drools.guvnor.vfs.Path}.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnStartPathParameterMethodName(final TypeElement classElement,
                                                           final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  new String[]{"org.drools.guvnor.vfs.Path"},
                                  OnStart.class );
    }

    /**
     * Get the method name annotated with {@code @OnMayClose}. The method must
     * be public, non-static, have a return-type of void and take zero
     * parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnMayCloseMethodName(final TypeElement classElement,
                                                 final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getBooleanMethodName( classElement,
                                     processingEnvironment,
                                     OnMayClose.class );
    }

    /**
     * Get the method name annotated with {@code @OnClose}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnCloseMethodName(final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnClose.class );
    }

    /**
     * Get the method name annotated with {@code @OnReveal}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnRevealMethodName(final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnReveal.class );
    }

    /**
     * Get the method name annotated with {@code @OnLostFocus}. The method must
     * be public, non-static, have a return-type of void and take zero
     * parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnLostFocusMethodName(final TypeElement classElement,
                                                  final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnLostFocus.class );
    }

    /**
     * Get the method name annotated with {@code @OnFocus}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnFocusMethodName(final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnFocus.class );
    }

    /**
     * Get the method name annotated with {@code @DefaultPosition}. The method
     * must be public, non-static, have a return-type of void and take zero
     * parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getDefaultPositionMethodName(final TypeElement classElement,
                                                      final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getDefaultPositionMethodName( classElement,
                                             processingEnvironment,
                                             DefaultPosition.class );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartTitle}. The
     * method must be public, non-static, have a return-type of void and take
     * zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getTitleMethodName(final TypeElement classElement,
                                            final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getStringMethodName( classElement,
                                    processingEnvironment,
                                    WorkbenchPartTitle.class );
    }

    /**
     * Get the method name annotated with {@code @WorkbenchPartView}. The method
     * must be public, non-static, have a return-type of void and take zero
     * parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getWidgetMethodName(final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getIsWidgetMethodName( classElement,
                                      processingEnvironment,
                                      WorkbenchPartView.class );
    }

    /**
     * Check whether the provided type extends IsWidget.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return
     */
    public static boolean getIsWidget(final TypeElement classElement,
                                      final ProcessingEnvironment processingEnvironment) {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        return typeUtils.isAssignable( classElement.asType(),
                                       requiredReturnType );
    }

    /**
     * Get the method name annotated with {@code @IsDirty}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getIsDirtyMethodName(final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getBooleanMethodName( classElement,
                                     processingEnvironment,
                                     IsDirty.class );
    }

    /**
     * Get the method name annotated with {@code @OnSave}. The method must be
     * public, non-static, have a return-type of void and take zero parameters.
     * 
     * @param classElement
     * @param processingEnvironment
     * @return null if none found
     * @throws GenerationException
     */
    public static String getOnSaveMethodName(final TypeElement classElement,
                                             final ProcessingEnvironment processingEnvironment) throws GenerationException {
        return getVoidMethodName( classElement,
                                  processingEnvironment,
                                  OnSave.class );
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of void and take zero parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getVoidMethodName(final TypeElement classElement,
                                            final ProcessingEnvironment processingEnvironment,
                                            final Class annotation) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isSameType( actualReturnType,
                                        requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of void and take parameters matching 
    // those provided.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getVoidMethodName(final TypeElement classElement,
                                            final ProcessingEnvironment processingEnvironment,
                                            final String[] parameterTypes,
                                            final Class annotation) throws GenerationException {

        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = typeUtils.getNoType( TypeKind.VOID );
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isSameType( actualReturnType,
                                        requiredReturnType ) ) {
                continue;
            }
            if ( !doParametersMatch( typeUtils,
                                     elementUtils,
                                     e,
                                     parameterTypes ) ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    //Check whether the ExecutableElement's parameter list matches the requiredParameterTypes.
    //For a match to be found the number of parameters must equal together with both the sequence 
    //and types. 
    private static boolean doParametersMatch(final Types typeUtils,
                                             final Elements elementUtils,
                                             final ExecutableElement e,
                                             final String[] requiredParameterTypes) {
        if ( e.getParameters().size() != requiredParameterTypes.length ) {
            return false;
        }
        List<TypeMirror> requiredTypes = new ArrayList<TypeMirror>();
        for ( String parameterType : requiredParameterTypes ) {
            requiredTypes.add( elementUtils.getTypeElement( parameterType ).asType() );
        }
        for ( int i = 0; i < requiredTypes.size(); i++ ) {
            final TypeMirror actualType = e.getParameters().get( i ).asType();
            final TypeMirror requiredType = requiredTypes.get( i );
            if ( !typeUtils.isAssignable( actualType,
                                          requiredType ) ) {
                return false;
            }
        }
        return true;
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of boolean and take zero
    // parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getBooleanMethodName(final TypeElement classElement,
                                               final ProcessingEnvironment processingEnvironment,
                                               final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.Boolean" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of String and take zero
    // parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getStringMethodName(final TypeElement classElement,
                                              final ProcessingEnvironment processingEnvironment,
                                              final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "java.lang.String" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of IsWidget and take zero
    // parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getIsWidgetMethodName(final TypeElement classElement,
                                                final ProcessingEnvironment processingEnvironment,
                                                final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "com.google.gwt.user.client.ui.IsWidget" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

    // Lookup a public method name with the given annotation. The method must be
    // public, non-static, have a return-type of Position and take zero
    // parameters.
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String getDefaultPositionMethodName(final TypeElement classElement,
                                                       final ProcessingEnvironment processingEnvironment,
                                                       final Class annotation) throws GenerationException {
        final Types typeUtils = processingEnvironment.getTypeUtils();
        final Elements elementUtils = processingEnvironment.getElementUtils();
        final TypeMirror requiredReturnType = elementUtils.getTypeElement( "org.drools.guvnor.client.workbench.Position" ).asType();
        final List<ExecutableElement> methods = ElementFilter.methodsIn( classElement.getEnclosedElements() );

        ExecutableElement match = null;
        for ( ExecutableElement e : methods ) {

            final TypeMirror actualReturnType = e.getReturnType();

            //Check method
            if ( e.getAnnotation( annotation ) == null ) {
                continue;
            }
            if ( !typeUtils.isAssignable( actualReturnType,
                                          requiredReturnType ) ) {
                continue;
            }
            if ( e.getParameters().size() != 0 ) {
                continue;
            }
            if ( e.getModifiers().contains( Modifier.STATIC ) ) {
                continue;
            }
            if ( !e.getModifiers().contains( Modifier.PUBLIC ) ) {
                continue;
            }
            if ( match != null ) {
                throw new GenerationException( "Multiple methods with @" + annotation.getSimpleName() + " detected." );
            }
            match = e;
        }
        if ( match == null ) {
            return null;
        }
        return match.getSimpleName().toString();
    }

}
