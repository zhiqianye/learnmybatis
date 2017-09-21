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
package org.apache.ibatis.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Clinton Begin
 */

/**
 * 可重用的执行器
 *
 * 执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，
 * 而是放置于Map<String, Statement>内，供下一次使用。（可以是Statement或PrepareStatement对象）
 * 直到提交或者回滚之前，执行flushStatement时才关闭并清除缓存
 */
public class ReuseExecutor extends BaseExecutor {

	//可重用的执行器内部用了一个map，用来缓存SQL语句对应的Statement，即<sql, statement>
	private final Map<String, Statement> statementMap = new HashMap<String, Statement>();

	public ReuseExecutor(Configuration configuration, Transaction transaction) {
		super(configuration, transaction);
	}

	@Override
	public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
		Configuration configuration = ms.getConfiguration();
		StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
		Statement stmt = prepareStatement(handler, ms.getStatementLog());
		return handler.update(stmt);
	}

	@Override
	public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		Configuration configuration = ms.getConfiguration();
		StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
		Statement stmt = prepareStatement(handler, ms.getStatementLog());
		return handler.<E>query(stmt, resultHandler);
	}

	@Override
	protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
		Configuration configuration = ms.getConfiguration();
		StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
		Statement stmt = prepareStatement(handler, ms.getStatementLog());
		return handler.<E>queryCursor(stmt);
	}

	//在执行commit、rollback等动作前，将会执行flushStatements()方法，将Statement对象逐一关闭
	@Override
	public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
		//关闭缓存statement，清空缓存
		for (Statement stmt : statementMap.values()) {
			closeStatement(stmt);
		}
		statementMap.clear();
		return Collections.emptyList();
	}

	private Statement prepareStatement(StatementHandler handler, Log statementLog) throws SQLException {
		Statement stmt;
		//得到绑定的SQL语句
		BoundSql boundSql = handler.getBoundSql();
		String sql = boundSql.getSql();
		//如果缓存中已经有了，直接得到Statement
		if (hasStatementFor(sql)) {
			stmt = getStatement(sql);
			applyTransactionTimeout(stmt);
		} else {
			//如果缓存没有找到，则和SimpleExecutor处理完全一样，然后加入缓存
			Connection connection = getConnection(statementLog);
			stmt = handler.prepare(connection, transaction.getTimeout());
			putStatement(sql, stmt);
		}
		handler.parameterize(stmt);
		return stmt;
	}

	private boolean hasStatementFor(String sql) {
		try {
			//存在且连接可用
			return statementMap.keySet().contains(sql) && !statementMap.get(sql).getConnection().isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	//map的get
	private Statement getStatement(String s) {
		return statementMap.get(s);
	}
	//map的put
	private void putStatement(String sql, Statement stmt) {
		statementMap.put(sql, stmt);
	}

}
