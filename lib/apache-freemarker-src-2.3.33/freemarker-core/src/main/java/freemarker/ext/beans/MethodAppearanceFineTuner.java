/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.ext.beans;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;

import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecision;
import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecisionInput;
import freemarker.template.MethodCallAwareTemplateHashModel;

/**
 * Used for customizing how the Java methods are visible from templates, via
 * {@link BeansWrapper#setMethodAppearanceFineTuner(MethodAppearanceFineTuner)}.
 * The object that implements this should also implement {@link SingletonCustomizer} whenever possible, to allow reusing
 * the class introspection cache in more situations.
 * 
 * @since 2.3.21
 */
public interface MethodAppearanceFineTuner {

    /**
     * Implement this to tweak certain aspects of how methods appear in the
     * data-model. {@link BeansWrapper} will pass in all Java methods here that
     * it intends to expose in the data-model as methods (so you can do
     * {@code obj.foo()} in the template). This also applies to JavaBeans property read methods, like
     * {@code public int getX()}, even if the bean property value itself is already exposed (with name {@code x}, in
     * this example).
     *
     * <p>With this method you can do the following tweaks:
     * <ul>
     *   <li>Hide a method that would be otherwise shown by calling
     *     {@link MethodAppearanceDecision#setExposeMethodAs(String)}
     *     with {@code null} parameter. Note that you can't un-hide methods
     *     that are not public or are considered to by unsafe
     *     (like {@link Object#wait()}) because
     *     {@link #process} is not called for those.</li>
     *   <li>Show the method with a different name in the data-model than its
     *     real name by calling
     *     {@link MethodAppearanceDecision#setExposeMethodAs(String)} with non-{@code null} parameter. (If set to
     *     {@code null}, then the method won't be exposed.) The default is the real name of the method.
     *     Note that if {@code methodInsteadOfPropertyValueBeforeCall} is {@code true}, the method is not exposed if the
     *     method name set here is the same as the name of the property set for this method with
     *     {@link MethodAppearanceDecision#setExposeAsProperty(PropertyDescriptor)}.
     *   <li>Create a fake JavaBean property for this method by calling
     *     {@link MethodAppearanceDecision#setExposeAsProperty(PropertyDescriptor)}.
     *     For example, if you have {@code int size()} in a class, but you
     *     want it to be accessed from the templates as {@code obj.size} (like a JavaBean property),
     *     rather than as {@code obj.size()}, you can do that with this (but remember calling
     *     {@link MethodAppearanceDecision#setMethodShadowsProperty(boolean) setMethodShadowsProperty(false)} as well,
     *     if the method name is exactly the same as the property name).
     *     The default is {@code null}, which means that no fake property is
     *     created for the method. You need not, and shouldn't set this
     *     to non-{@code null} for the property read (get/is) methods of real JavaBeans
     *     properties, as bean properties are not treated as methods (hence the {@link MethodAppearanceFineTuner} is
     *     irrelevant), and are exposed or not regardless of this mechanism (based on
     *     {@link BeansWrapperBuilder#setExposureLevel(int)}).
     *     The property name in the {@link PropertyDescriptor} can be anything,
     *     but the method (or methods) in it must belong to the class that
     *     is given as the {@code clazz} parameter, or it must be inherited from
     *     that class, otherwise the behavior is undefined, and errors can occur later.
     *     {@link IndexedPropertyDescriptor}-s are supported.
     *     If a real JavaBeans property of the same name exists, or a fake property
     *     of the same name was already assigned earlier, it won't be
     *     replaced by the new one by default, however this can be changed with
     *     {@link MethodAppearanceDecision#setReplaceExistingProperty(boolean)}.
     *   <li>If something is exposed as property via
     *     {@link MethodAppearanceDecision#setExposeAsProperty(PropertyDescriptor)} (and not only because it's a real
     *     JavaBeans property), and you also want the property value to be accessible in templates as the return value
     *     of a 0-argument method of the same name, then call
     *     {@link MethodAppearanceDecision#setMethodInsteadOfPropertyValueBeforeCall(boolean)} with {@code true}.
     *     Here's an example to explain that. Let's say, you have a class that contains "public String name()", and you
     *     exposed that as a property via {@link MethodAppearanceDecision#setExposeAsProperty(PropertyDescriptor)}. So
     *     far, you can access the property value from templates as {@code user.name}, but {@code user.name()} will
     *     fail, saying that you try to call a {@code String} (because you apply the {@code ()} operator on the result
     *     of {@code user.name}, which is a {@code String}). But with
     *     {@link MethodAppearanceDecision#setMethodInsteadOfPropertyValueBeforeCall(boolean)} {@code true},
     *     both {@code user.name}, and {@code user.name()} will do the same (which is possible because if {@code user}
     *     is a {@link MethodCallAwareTemplateHashModel}, "name" can be resoled to different values in the two cases).
     *     The default (initial) value of {@code methodInsteadOfPropertyValueBeforeCall} depends on
     *     {@link BeansWrapperConfiguration#setDefaultZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy)},
     *     and {@link BeansWrapperConfiguration#setRecordZeroArgumentNonVoidMethodPolicy(ZeroArgumentNonVoidMethodPolicy)}.
     *   <li>Prevent the method to hide a JavaBeans property (fake or real) of the same name by calling
     *     {@link MethodAppearanceDecision#setMethodShadowsProperty(boolean)} with {@code false}.
     *     The default is {@code true}, so if you have both a property and a method called "foo", then in the template
     *     {@code myObject.foo} will return the method itself instead of the property value, which is often undesirable.
     * </ul>
     *
     * @param in Describes the method about which the decision will be made.
     *  
     * @param out Stores how the method will be exposed in the data-model after {@link #process} returns.
     *   This is initialized so that it reflects the default
     *   behavior of {@link BeansWrapper}, so you don't have to do anything with this
     *   when you don't want to change the default behavior.
     */
    void process(MethodAppearanceDecisionInput in, MethodAppearanceDecision out);    
    
}
