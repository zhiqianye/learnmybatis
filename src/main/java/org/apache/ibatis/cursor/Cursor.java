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
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * Cursor SQL queries must be ordered (resultOrdered="true") using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */

/**
 * 游标可以实现获取数据条目的懒加载效果
 * 当查询大量（上百万）数据的时候，使用游标可以有效的减少内存使用，不需要一次性将所有数据得到，可以通过游标逐个或者分批（逐个获取一批后）处理。
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

    /**
     * true-游标开始从数据库获取数据
     * @return true if the cursor has started to fetch items from database.
     */
    boolean isOpen();

    /**
     *  true-游标已经被完全消费并返回了全部元素
     * @return true if the cursor is fully consumed and has returned all elements matching the query.
     */
    boolean isConsumed();

    /**
     * 当前条目的索引号，从0开始。如果一个元素都没获取，则返回-1
     * Get the current item index. The first item has the index 0.
     * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
     */
    int getCurrentIndex();
}
