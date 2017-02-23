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

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.session.Configuration;

/**
 * @author Clinton Begin
 * @author Simone Tripodi
 */

/**
 * 类型处理器的基类
 * @param <T>
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

    //mybatis配置实例，在这个类里没有用到
    protected Configuration configuration;

    public void setConfiguration(Configuration c) {
        this.configuration = c;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            //java参数为空
            if (jdbcType == null) {
                //sql数据类型也为空，则抛出异常，因为没办法设置sql数据值
                throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
            }
            try {
                //对ps的第i个参数设置为SQL的NULL
                ps.setNull(i, jdbcType.TYPE_CODE);
            } catch (SQLException e) {
                throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . " +
                        "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " +
                        "Cause: " + e, e);
            }
        } else {
            try {
                setNonNullParameter(ps, i, parameter, jdbcType);
            } catch (Exception e) {
                throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . " +
                        "Try setting a different JdbcType for this parameter or a different configuration property. " +
                        "Cause: " + e, e);
            }
        }
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        T result;
        try {
            result = getNullableResult(rs, columnName);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
        }
        if (rs.wasNull()) {
            //SQL使用NULL值和Java使用null是不同的概念，需要使用wasNull方法判断，该方法用于反馈最后一次读取的数据库值否为NULL
            return null;
        } else {
            return result;
        }
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        T result;
        try {
            result = getNullableResult(rs, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex+ " from result set.  Cause: " + e, e);
        }
        if (rs.wasNull()) {
            return null;
        } else {
            return result;
        }
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        T result;
        try {
            result = getNullableResult(cs, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex+ " from callable statement.  Cause: " + e, e);
        }
        if (cs.wasNull()) {
            return null;
        } else {
            return result;
        }
    }

    /**
     * 对于java参数非空的情况，交给子类进行设置，因为每一个子类都是用于处理[java类型-SQL类型]的关系
     */
    public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    /**
     * 以下3个方法是取得可能为null的结果，具体交给子类完成
     */
    public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

    public abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

    public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;

}
