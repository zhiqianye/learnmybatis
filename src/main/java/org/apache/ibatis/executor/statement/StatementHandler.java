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
package org.apache.ibatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.ResultHandler;

/**
 * @author Clinton Begin
 */

/**
 * Statement处理器
 */
public interface StatementHandler {

	//准备Statement
	Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

	//准备参数
	void parameterize(Statement statement) throws SQLException;

	//批处理
	void batch(Statement statement)	throws SQLException;

	//更新
	int update(Statement statement)	throws SQLException;

	//查询，并将结果交给resultHandler
	<E> List<E> query(Statement statement, ResultHandler resultHandler)	throws SQLException;

	//查询游标
	<E> Cursor<E> queryCursor(Statement statement) throws SQLException;

	//获取BoundSql
	//BoundSql对象存储String sql，而BoundSql则由StatementHandler对象获取
	BoundSql getBoundSql();

	//获取参数处理器
	ParameterHandler getParameterHandler();

}
