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

package ${packageName};

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.guvnor.client.editors.test.TestPresenter;
import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.NameToken;
import org.drools.guvnor.client.mvp.StaticScreenService;
<#if getDefaultPositionMethodName??>
import org.drools.guvnor.client.workbench.Position;
</#if>

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
@NameToken("${tokenName}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractStaticScreenActivity
    implements
    StaticScreenService {

    @Inject
    private ${realClassName} realPresenter;

    @Override
    public String getNameToken() {
        return "${tokenName}";
    }

    @Override
    public void onStart() {
        <#if onStartMethodName??>
        realPresenter.${onStartMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnStart annotation
        </#if>
    }

    @Override
    public boolean mayClose() {
        <#if mayCloseMethodName??>
        return realPresenter.${mayCloseMethodName}();
        <#else>
        return true;
        </#if>
    }

    @Override
    public void onClose() {
        <#if onCloseMethodName??>
        realPresenter.${onCloseMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnClose annotation
        </#if>
    }

    @Override
    public void onReveal() {
        <#if onRevealMethodName??>
        realPresenter.${onRevealMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnReveal annotation
        </#if>
    }

    @Override
    public void onLostFocus() {
        <#if onLostFocusMethodName??>
        realPresenter.${onLostFocusMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnLostFocus annotation
        </#if>
    }

    @Override
    public void onFocus() {
        <#if onFocusMethodName??>
        realPresenter.${onFocusMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @OnFocus annotation
        </#if>
    }

    @Override
    public String getTitle() {
        <#if getTitleMethodName??>
        return realPresenter.${getTitleMethodName}();
        <#else>
        //Do nothing. The real presenter does not have a @Title annotation
        return null;
        </#if>
    }

    @Override
    public IsWidget getWidget() {
        <#if getWidgetMethodName??>
        return realPresenter.${getWidgetMethodName}();
        <#elseif isWidget>
        return realPresenter;
        <#else>
        return null;
        </#if>
    }
    
    <#if getDefaultPositionMethodName??>
    @Override
    public Position getDefaultPosition() {
        return realPresenter.${getDefaultPositionMethodName}();
    }
    
    </#if>
    @Override
    public StaticScreenService getPresenter() {
        return this;
    }

}
