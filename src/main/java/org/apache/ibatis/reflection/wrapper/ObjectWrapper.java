/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * @author Clinton Begin
 */

/**
 * 对象包装器，从而对Object的常用操作，都可以转换到ObjectWrapper上
 */
public interface ObjectWrapper {

    //获取属性
    Object get(PropertyTokenizer prop);

    //设置属性
    void set(PropertyTokenizer prop, Object value);

    //寻找属性
    String findProperty(String name, boolean useCamelCaseMapping);

    //获取getter列表
    String[] getGetterNames();

    //获取setter列表
    String[] getSetterNames();

    //获取name属性的类型
    Class<?> getSetterType(String name);

    //获取name属性的类型？
    Class<?> getGetterType(String name);

    //是否有指定的setter
    boolean hasSetter(String name);

    //是否有指定的getter
    boolean hasGetter(String name);

    //实例化属性
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    //是否集合
    boolean isCollection();

    //增加一个属性
    void add(Object element);

    //增加多个属性
    <E> void addAll(List<E> element);

}
