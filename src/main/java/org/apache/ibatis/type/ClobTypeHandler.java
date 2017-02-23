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

import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Clinton Begin
 */
/**
 * Clob类型与String类型的转换处理器
 * 重点是setClob和getClob，以及Clob与Reader之间的转化。
 * 设置时，使用setClob(Reader)方式；读取时，使用getClob().getCharacterStream方式
 * MARK 可以与ClobReaderTypeHandler对比，感觉就是字节流和字符流的区别
 *
 */
public class ClobTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        StringReader reader = new StringReader(parameter);
        //TODO 这个和ps.setClob(i, reader);有啥区别？？
        ps.setCharacterStream(i, reader, parameter.length());
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnName);
        if (clob != null) {
            int size = (int) clob.length();
            //TODO 忽略第一个字符？
            value = clob.getSubString(1, size);
        }
        return value;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = "";
        Clob clob = rs.getClob(columnIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1, size);
        }
        return value;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = "";
        Clob clob = cs.getClob(columnIndex);
        if (clob != null) {
            int size = (int) clob.length();
            value = clob.getSubString(1, size);
        }
        return value;
    }
}
