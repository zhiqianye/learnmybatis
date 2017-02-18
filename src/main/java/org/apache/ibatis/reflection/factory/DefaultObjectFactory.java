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
package org.apache.ibatis.reflection.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.ibatis.reflection.ReflectionException;

/**
 * @author Clinton Begin
 */

/**
 * 默认的ObjectFactory实现类
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable {

    private static final long serialVersionUID = -8855120656740914948L;

    @Override
    public <T> T create(Class<T> type) {
        // 使用更细粒度的重载方法
        return create(type, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        //先对对象类型进行一定转换后，再进行实例化
        Class<?> classToCreate = resolveInterface(type);
        // we know types are assignable
        return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
    }

    @Override
    public void setProperties(Properties properties) {
        // no props for default
        //默认没有配置
    }

    <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        try {
            Constructor<T> constructor;
            if (constructorArgTypes == null || constructorArgs == null) {
                //传入参数没有构造器参数类型或者参数值，则使用默认构造器
                constructor = type.getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance();
            }
            //使用指定参数类型和值进行构造
            constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[constructorArgTypes.size()]));
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
        } catch (Exception e) {
            StringBuilder argTypes = new StringBuilder();
            if (constructorArgTypes != null && !constructorArgTypes.isEmpty()) {
                //如果有参数类型，则追加到StringBuilder中用于打印详细信息
                for (Class<?> argType : constructorArgTypes) {
                    argTypes.append(argType.getSimpleName());
                    argTypes.append(",");
                }
                argTypes.deleteCharAt(argTypes.length() - 1); // remove trailing ,
            }
            StringBuilder argValues = new StringBuilder();
            if (constructorArgs != null && !constructorArgs.isEmpty()) {
                //如果有参数值，则追加到StringBuilder中用于打印详细信息
                for (Object argValue : constructorArgs) {
                    argValues.append(String.valueOf(argValue));
                    argValues.append(",");
                }
                argValues.deleteCharAt(argValues.length() - 1); // remove trailing ,
            }
            throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values (" + argValues + "). Cause: " + e, e);
        }
    }

    /**
     * 类型解析（转换）
     * @param type 对象类型
     * @return 转换后的对象类型
     */
    protected Class<?> resolveInterface(Class<?> type) {
        Class<?> classToCreate;
        if (type == List.class || type == Collection.class || type == Iterable.class) {
            //List，Collection和Iterable类型，统一使用ArrayList实现
            classToCreate = ArrayList.class;
        } else if (type == Map.class) {
            //Map类型使用HashMap实现
            classToCreate = HashMap.class;
        } else if (type == SortedSet.class) { // issue #510 Collections Support
            //FIXME SortedSet类型使用TreeSet实现?
            classToCreate = TreeSet.class;
        } else if (type == Set.class) {
            //Set类型使用HashSet实现
            classToCreate = HashSet.class;
        } else {
            //其他类型保持不变
            classToCreate = type;
        }
        return classToCreate;
    }

    @Override
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

}
