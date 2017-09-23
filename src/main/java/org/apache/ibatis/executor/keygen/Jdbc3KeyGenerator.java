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
package org.apache.ibatis.executor.keygen;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Clinton Begin
 */
/**
 * JDBC3键值生成器,核心是使用JDBC3的Statement.getGeneratedKeys()
 * 用于处理数据库支持自增主键的情况，如MySQL的auto_increment
 *
 */
public class Jdbc3KeyGenerator implements KeyGenerator {

	@Override
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		// do nothing
	}

	@Override
	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		processBatch(ms, stmt, getParameters(parameter));
	}

	public void processBatch(MappedStatement ms, Statement stmt, Collection<Object> parameters) {
		ResultSet rs = null;
		try {
			rs = stmt.getGeneratedKeys();
			final Configuration configuration = ms.getConfiguration();
			final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			final String[] keyProperties = ms.getKeyProperties();
			final ResultSetMetaData rsmd = rs.getMetaData();
			TypeHandler<?>[] typeHandlers = null;
			if (keyProperties != null && rsmd.getColumnCount() >= keyProperties.length) {
				for (Object parameter : parameters) {
					// there should be one row for each statement (also one for each parameter)
					if (!rs.next()) {
						break;
					}
					final MetaObject metaParam = configuration.newMetaObject(parameter);
					if (typeHandlers == null) {
						typeHandlers = getTypeHandlers(typeHandlerRegistry, metaParam, keyProperties, rsmd);
					}
					populateKeys(rs, metaParam, keyProperties, typeHandlers);
				}
			}
		} catch (Exception e) {
			throw new ExecutorException("Error getting generated key or setting result to parameter object. Cause: " + e, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	//从Object类型的参数中，转化为集合类型
	private Collection<Object> getParameters(Object parameter) {
		Collection<Object> parameters = null;
		if (parameter instanceof Collection) {
			parameters = (Collection) parameter;
		} else if (parameter instanceof Map) {
			Map parameterMap = (Map) parameter;
			if (parameterMap.containsKey("collection")) {
				parameters = (Collection) parameterMap.get("collection");
			} else if (parameterMap.containsKey("list")) {
				parameters = (List) parameterMap.get("list");
			} else if (parameterMap.containsKey("array")) {
				parameters = Arrays.asList((Object[]) parameterMap.get("array"));
			}
		}
		if (parameters == null) {
			parameters = new ArrayList<Object>();
			parameters.add(parameter);
		}
		return parameters;
	}

	private TypeHandler<?>[] getTypeHandlers(TypeHandlerRegistry typeHandlerRegistry, MetaObject metaParam, String[] keyProperties, ResultSetMetaData rsmd) throws SQLException {
		TypeHandler<?>[] typeHandlers = new TypeHandler<?>[keyProperties.length];
		for (int i = 0; i < keyProperties.length; i++) {
			if (metaParam.hasSetter(keyProperties[i])) {
				Class<?> keyPropertyType = metaParam.getSetterType(keyProperties[i]);
				TypeHandler<?> th = typeHandlerRegistry.getTypeHandler(keyPropertyType, JdbcType.forCode(rsmd.getColumnType(i + 1)));
				typeHandlers[i] = th;
			}
		}
		return typeHandlers;
	}

	//填充key
	private void populateKeys(ResultSet rs, MetaObject metaParam, String[] keyProperties, TypeHandler<?>[] typeHandlers) throws SQLException {
		for (int i = 0; i < keyProperties.length; i++) {
			TypeHandler<?> th = typeHandlers[i];
			if (th != null) {
				Object value = th.getResult(rs, i + 1);
				metaParam.setValue(keyProperties[i], value);
			}
		}
	}

}
