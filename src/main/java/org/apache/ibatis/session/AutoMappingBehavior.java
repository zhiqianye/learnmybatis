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
package org.apache.ibatis.session;

/**
 * Specifies if and how MyBatis should automatically map columns to fields/properties.
 *
 * @author Eduardo Macarron
 */

/**
 * 自动映射
 */
public enum AutoMappingBehavior {

    /**
     * Disables auto-mapping.
     */
    //禁止自动映射
    NONE,

    /**
     * Will only auto-map results with no nested result mappings defined inside.
     */
    // 只会自动映射没有定义嵌套结果集映射的结果集
    PARTIAL,

    /**
     * Will auto-map result mappings of any complexity (containing nested or otherwise).
     */
    //自动映射任意复杂的结果集（无论是否嵌套）
    FULL
}