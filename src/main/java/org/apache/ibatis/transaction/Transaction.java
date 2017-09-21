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
package org.apache.ibatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wraps a database connection.
 * Handles the connection lifecycle that comprises: its creation, preparation, commit/rollback and close.
 *
 * @author Clinton Begin
 */

/**
 * 事务，包装了一个Connection, 包含commit,rollback,close方法
 * Executor在执行数据库操作时，与事务的提交、回滚、关闭毫无瓜葛（方法内部不会提交、回滚事务），
 * 需要手动显示调用commit()、rollback()、close()等方法。
 *
 * <br/>如果没有执行commit()，仅执行close()，会发生什么？close时会回滚
 *
 * <br/>如果即不commit，也不close，会发生什么？insert后，jvm结束前，如果事务隔离级别是read uncommitted，我们可以查到该条记录。
 * jvm结束后，事务被rollback()，记录消失。通过断点debug方式，你可以看到效果。
 *
 */
public interface Transaction {

    /**
     * Retrieve inner database connection
     * @return DataBase connection
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     * @throws SQLException
     */
    void close() throws SQLException;

    /**
     * Get transaction timeout if set
     * @throws SQLException
     */
    Integer getTimeout() throws SQLException;

}
