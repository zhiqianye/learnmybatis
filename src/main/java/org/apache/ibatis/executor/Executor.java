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
package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * @author Clinton Begin
 */

/**
 * 执行器接口
 * 在Mybatis中，SqlSession对数据库的操作，<b>将委托给执行器Executor来完成</b>，而Executor由五鼠组成：
 * <br/>简单鼠SimpleExecutor
 * <br/>重用鼠ReuseExecutor
 * <br/>批量鼠BatchExecutor
 * <br/>缓存鼠CachingExecutor
 * <br/>无用鼠ClosedExecutor：BaseExecutor的静态内部类
 */
public interface Executor {

    //空ResultHandler
    ResultHandler NO_RESULT_HANDLER = null;

	//更新
    int update(MappedStatement ms, Object parameter) throws SQLException;

	//查询，待分页，缓存，和BoundSql
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

	//查询，带分页
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

	//
    <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

	//刷新批批处理语句
    List<BatchResult> flushStatements() throws SQLException;

	//提交和回滚，参数是是否要强制
    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

	//创建CacheKey
    CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

	//判断是否缓存了
    boolean isCached(MappedStatement ms, CacheKey key);

    void clearLocalCache();

	//延迟加载
    void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

    Transaction getTransaction();

    void close(boolean forceRollback);

    boolean isClosed();

    void setExecutorWrapper(Executor executor);

}
