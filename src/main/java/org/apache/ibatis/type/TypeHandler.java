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
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */

/**
 * 类型处理器。
 * 总体而言分为两个方法，setParameter和getResult
 * setParameter应用于ParameterHandler转换设置参数
 * getResult应用于ResultSetHandler转换获取结果
 *
 * @param <T> 设置或获取值的类型
 */
public interface TypeHandler<T> {

    //设置参数
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    //在结果集中，通过列名获取结果
    T getResult(ResultSet rs, String columnName) throws SQLException;

    //在结果集中，通过列索引号获取结果
    T getResult(ResultSet rs, int columnIndex) throws SQLException;

    //FIXME 从CallableStatement（存储过程Statement）中获取结果
    T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}