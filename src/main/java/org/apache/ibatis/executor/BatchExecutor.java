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

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Jeff Butler
 */

/**
 *
 * 执行update（没有select，JDBC批处理不支持select），将所有sql都添加到批处理中（addBatch()），
 * 等待统一执行（executeBatch()），它缓存了多个Statement对象，每个Statement对象都是addBatch()完毕后，
 * 等待逐一执行executeBatch()批处理的；BatchExecutor相当于维护了多个桶，每个桶里都装了很多属于自己的SQL，
 * 就像苹果蓝里装了很多苹果，番茄蓝里装了很多番茄，最后，再统一倒进仓库。（可以是Statement或PrepareStatement对象）
 *
 *
 *
 * BatchExecutor和JDBC批处理的区别。
 * JDBC:
 * 对于Statement来说，只要SQL不同，就会产生新编译动作，Statement不支持问号“?”参数占位符。
 * 对于PrepareStatement，只要SQL相同，就只会编译一次，如果SQL不同呢？此时和Statement一样，会编译多次。PrepareStatement的优势在于支持问号“?”参数占位符，SQL相同，参数不同时，可以减少编译次数至一次，大大提高效率；另外可以防止SQL注入漏洞。
 * BatchExecutor:
 * BatchExecutor的批处理，和JDBC的批处理，主要区别就是BatchExecutor维护了一组Statement批处理对象，它有自动路由功能，SQL1、SQL2、SQL3代表不同的SQL。（Statement或Preparestatement）
 */
public class BatchExecutor extends BaseExecutor {

	public static final int BATCH_UPDATE_RETURN_VALUE = Integer.MIN_VALUE + 1002;
	// 缓存多个Statement对象，每个Statement都是addBatch()后，等待执行
	private final List<Statement> statementList = new ArrayList<Statement>();
	// 对应的结果集（主要保存了update结果的count数量）
	private final List<BatchResult> batchResultList = new ArrayList<BatchResult>();
	// 当前保存的sql，即上次执行的sql
	private String currentSql;
	private MappedStatement currentStatement;

	public BatchExecutor(Configuration configuration, Transaction transaction) {
		super(configuration, transaction);
	}

	/*
		注：对于批处理来说，JDBC只支持update操作（update、insert、delete等），不支持select查询操作。
	 */
	@Override
	public int doUpdate(MappedStatement ms, Object parameterObject) throws SQLException {
		final Configuration configuration = ms.getConfiguration();
		final StatementHandler handler = configuration.newStatementHandler(this, ms, parameterObject, RowBounds.DEFAULT, null, null);
		final BoundSql boundSql = handler.getBoundSql();
		// 本次执行的sql
		final String sql = boundSql.getSql();
		final Statement stmt;
		// 要求当前的sql和上一次的currentSql相同，同时MappedStatement也必须相同
		if (sql.equals(currentSql) && ms.equals(currentStatement)) {
			/*
				需要注意的是sql.equals(currentSql)和statementList.get(last)，充分说明了其有序逻辑：
				AABB，将生成2个Statement对象；
				AABBAA，将生成3个Statement对象，而不是2个。
				因为，只要sql有变化，将导致生成新的Statement对象。
			 */
			int last = statementList.size() - 1;
			stmt = statementList.get(last);
			applyTransactionTimeout(stmt);
			handler.parameterize(stmt);//fix Issues 322
			BatchResult batchResult = batchResultList.get(last);
			batchResult.addParameterObject(parameterObject);
		} else {
			// 尚不存在，新建Statement
			Connection connection = getConnection(ms.getStatementLog());
			stmt = handler.prepare(connection, transaction.getTimeout());
			handler.parameterize(stmt);    //fix Issues 322
			currentSql = sql;
			currentStatement = ms;
			// 放到Statement缓存
			statementList.add(stmt);
			batchResultList.add(new BatchResult(ms, sql, parameterObject));
		}
		// handler.parameterize(stmt);
		handler.batch(stmt);
		return BATCH_UPDATE_RETURN_VALUE;
	}

	@Override
	public <E> List<E> doQuery(MappedStatement ms, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)
			throws SQLException {
		Statement stmt = null;
		try {
			flushStatements();
			Configuration configuration = ms.getConfiguration();
			StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameterObject, rowBounds, resultHandler, boundSql);
			Connection connection = getConnection(ms.getStatementLog());
			stmt = handler.prepare(connection, transaction.getTimeout());
			handler.parameterize(stmt);
			return handler.<E>query(stmt, resultHandler);
		} finally {
			closeStatement(stmt);
		}
	}

	@Override
	protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql) throws SQLException {
		flushStatements();
		Configuration configuration = ms.getConfiguration();
		StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
		Connection connection = getConnection(ms.getStatementLog());
		Statement stmt = handler.prepare(connection, transaction.getTimeout());
		handler.parameterize(stmt);
		return handler.<E>queryCursor(stmt);
	}

	/*
		完成执行stmt.executeBatch()，随即关闭这些Statement对象
	 */
	@Override
	public List<BatchResult> doFlushStatements(boolean isRollback) throws SQLException {
		try {
			List<BatchResult> results = new ArrayList<BatchResult>();
			if (isRollback) {
				return Collections.emptyList();
			}
			for (int i = 0, n = statementList.size(); i < n; i++) {
				Statement stmt = statementList.get(i);
				applyTransactionTimeout(stmt);
				BatchResult batchResult = batchResultList.get(i);
				try {
					batchResult.setUpdateCounts(stmt.executeBatch());
					MappedStatement ms = batchResult.getMappedStatement();
					List<Object> parameterObjects = batchResult.getParameterObjects();
					KeyGenerator keyGenerator = ms.getKeyGenerator();
					if (Jdbc3KeyGenerator.class.equals(keyGenerator.getClass())) {
						Jdbc3KeyGenerator jdbc3KeyGenerator = (Jdbc3KeyGenerator) keyGenerator;
						jdbc3KeyGenerator.processBatch(ms, stmt, parameterObjects);
					} else if (!NoKeyGenerator.class.equals(keyGenerator.getClass())) { //issue #141
						for (Object parameter : parameterObjects) {
							keyGenerator.processAfter(this, ms, stmt, parameter);
						}
					}
				} catch (BatchUpdateException e) {
					StringBuilder message = new StringBuilder();
					message.append(batchResult.getMappedStatement().getId())
							.append(" (batch index #")
							.append(i + 1)
							.append(")")
							.append(" failed.");
					if (i > 0) {
						message.append(" ")
								.append(i)
								.append(" prior sub executor(s) completed successfully, but will be rolled back.");
					}
					throw new BatchExecutorException(message.toString(), e, results, batchResult);
				}
				results.add(batchResult);
			}
			return results;
		} finally {
			for (Statement stmt : statementList) {
				closeStatement(stmt);
			}
			currentSql = null;
			statementList.clear();
			batchResultList.clear();
		}
	}

}
