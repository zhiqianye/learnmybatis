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
 * @author Clinton Begin
 */

/**
 * 记录边界
 * 通过传递RowBounds对象，来进行数据库数据的分页操作，然而遗憾的是，
 * 该分页操作是对ResultSet结果集进行分页，也就是人们常说的逻辑分页，而非物理分页。
 *
 * JDBC驱动并不是把所有结果加载至内存中，而是只加载小部分数据至内存中，
 * 如果还需要从数据库中取更多记录，它会再次去获取部分数据，这就是fetch size的用处。
 * 因此，Mybatis的逻辑分页性能，并不像很多人想的那么差，很多人认为是对内存进行的分页。
 *
 *
 */
public class RowBounds {

    public static final int NO_ROW_OFFSET = 0;
    public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
    public static final RowBounds DEFAULT = new RowBounds();

    private int offset;
    private int limit;

    public RowBounds() {
        //默认偏移量为0，不限制记录条数
        this.offset = NO_ROW_OFFSET;
        this.limit = NO_ROW_LIMIT;
    }

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

}
