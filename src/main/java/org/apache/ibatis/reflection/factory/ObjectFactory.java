/**
 *    Copyright 2009-2016 the original author or authors.
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

import java.util.List;
import java.util.Properties;

/**
 * MyBatis uses an ObjectFactory to create all needed new Objects.
 *
 * @author Clinton Begin
 */

/**
 * MyBatis使用ObjectFactory类创建所有对象，
 * 经过跟踪代码发现使用的根源为MetaObject，生成对象时使用
 */
public interface ObjectFactory {

    /**
     * Sets configuration properties.
     * @param properties configuration properties
     */
    /**
     * 设置ObjectFactory的配置
     * @param properties 配置对象
     */
    void setProperties(Properties properties);

    /**
     * Creates a new object with default constructor.
     * @param type Object type
     * @return
     */
    /**
     * 创建type类型的对象，使用默认构造器
     * @param type 对象类型
     * @return 对象实例
     */
    <T> T create(Class<T> type);

    /**
     * Creates a new object with the specified constructor and params.
     * @param type Object type
     * @param constructorArgTypes Constructor argument types
     * @param constructorArgs Constructor argument values
     * @return
     */
    /**
     * 创建type类型的对象，使用指定的构造器和参数
     * @param type 对象类型
     * @param constructorArgTypes 构造器参数类型
     * @param constructorArgs 构造器参数值
     * @return 对象实例
     */
    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    /**
     * Returns true if this object can have a set of other objects.
     * It's main purpose is to support non-java.util.Collection objects like Scala collections.
     *
     * @param type Object type
     * @return whether it is a collection or not
     * @since 3.1.0
     */
    /**
     * type类是否是其他对象的集合。主要目的是支持非java.util.Collection的对象，比如Scala collections
     * @param type 对象类型
     * @return true-是；false-否
     */
    <T> boolean isCollection(Class<T> type);

}
