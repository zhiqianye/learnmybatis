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
package org.apache.ibatis.jdbc;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * @author Clinton Begin
 */
/**
 * 脚本运行器,可以运行SQL脚本，如建表，插入数据，作为单元测试的前期准备
 * 这个类其实可以被所有项目的单元测试作为工具所利用
 */
public class ScriptRunner {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private static final String DEFAULT_DELIMITER = ";";

    private Connection connection;

    private boolean stopOnError;
    private boolean throwWarning;
    private boolean autoCommit;
    //是否使用全量脚本
    private boolean sendFullScript;
    private boolean removeCRs;
    //逃逸字符开关设置
    private boolean escapeProcessing = true;

    private PrintWriter logWriter = new PrintWriter(System.out);
    private PrintWriter errorLogWriter = new PrintWriter(System.err);

    private String delimiter = DEFAULT_DELIMITER;
    private boolean fullLineDelimiter = false;

    public ScriptRunner(Connection connection) {
        this.connection = connection;
    }

    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    public void setThrowWarning(boolean throwWarning) {
        this.throwWarning = throwWarning;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setSendFullScript(boolean sendFullScript) {
        this.sendFullScript = sendFullScript;
    }

    public void setRemoveCRs(boolean removeCRs) {
        this.removeCRs = removeCRs;
    }

    /**
     * @since 3.1.1
     */
    public void setEscapeProcessing(boolean escapeProcessing) {
        this.escapeProcessing = escapeProcessing;
    }

    public void setLogWriter(PrintWriter logWriter) {
        this.logWriter = logWriter;
    }

    public void setErrorLogWriter(PrintWriter errorLogWriter) {
        this.errorLogWriter = errorLogWriter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setFullLineDelimiter(boolean fullLineDelimiter) {
        this.fullLineDelimiter = fullLineDelimiter;
    }

    public void runScript(Reader reader) {
        setAutoCommit();

        try {
            if (sendFullScript) {
                executeFullScript(reader);
            } else {
                executeLineByLine(reader);
            }
        } finally {
            rollbackConnection();
        }
    }

    //执行全量脚本
    private void executeFullScript(Reader reader) {
        StringBuilder script = new StringBuilder();
        try {
            BufferedReader lineReader = new BufferedReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                script.append(line);
                script.append(LINE_SEPARATOR);
            }
            String command = script.toString();
            println(command);
            executeStatement(command);
            commitConnection();
        } catch (Exception e) {
            String message = "Error executing: " + script + ".  Cause: " + e;
            printlnError(message);
            throw new RuntimeSqlException(message, e);
        }
    }

	//执行行脚本，没输入一行，只要找到了命令结束符";"则立即执行
    private void executeLineByLine(Reader reader) {
        StringBuilder command = new StringBuilder();
        try {
            BufferedReader lineReader = new BufferedReader(reader);
            String line;
            while ((line = lineReader.readLine()) != null) {
                command = handleLine(command, line);
            }
            commitConnection();
            checkForMissingLineTerminator(command);
        } catch (Exception e) {
            String message = "Error executing: " + command + ".  Cause: " + e;
            printlnError(message);
            throw new RuntimeSqlException(message, e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            // ignore
        }
    }

    private void setAutoCommit() {
        try {
            //Clinton begins喜欢先比较，不同了再设置，考虑的是性能？
            if (autoCommit != connection.getAutoCommit()) {
                connection.setAutoCommit(autoCommit);
            }
        } catch (Throwable t) {
            throw new RuntimeSqlException("Could not set AutoCommit to " + autoCommit + ". Cause: " + t, t);
        }
    }

    private void commitConnection() {
        try {
            //不是自动提交，则提交
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (Throwable t) {
            throw new RuntimeSqlException("Could not commit transaction. Cause: " + t, t);
        }
    }

    private void rollbackConnection() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
            }
        } catch (Throwable t) {
            // ignore
        }
    }

	//校验是否命令行有遗留没有执行到
    private void checkForMissingLineTerminator(StringBuilder command) {
        if (command != null && command.toString().trim().length() > 0) {
            throw new RuntimeSqlException("Line missing end-of-line terminator (" + delimiter + ") => " + command);
        }
    }

    private StringBuilder handleLine(StringBuilder command, String line) throws SQLException, UnsupportedEncodingException {
        String trimmedLine = line.trim();
        if (lineIsComment(trimmedLine)) {
			//如果是注释
            final String cleanedString = trimmedLine.substring(2).trim().replaceFirst("//", "");
            if(cleanedString.toUpperCase().startsWith("@DELIMITER")) {
                delimiter = cleanedString.substring(11,12);
                return command;
            }
            println(trimmedLine);
        } else if (commandReadyToExecute(trimmedLine)) {
			//当前行找到了命令结束符";"，则将结束之前的语句添加并执行
            command.append(line.substring(0, line.lastIndexOf(delimiter)));
            command.append(LINE_SEPARATOR);
            println(command);
            executeStatement(command.toString());
            command.setLength(0);
        } else if (trimmedLine.length() > 0) {
            command.append(line);
            command.append(LINE_SEPARATOR);
        }
        return command;
    }

	//这一行是否为注释
    private boolean lineIsComment(String trimmedLine) {
        return trimmedLine.startsWith("//") || trimmedLine.startsWith("--");
    }

    private boolean commandReadyToExecute(String trimmedLine) {
        // issue #561 remove anything after the delimiter
        return !fullLineDelimiter && trimmedLine.contains(delimiter) || fullLineDelimiter && trimmedLine.equals(delimiter);
    }

    //执行脚本核心
    private void executeStatement(String command) throws SQLException {
        boolean hasResults = false;
        Statement statement = connection.createStatement();
        statement.setEscapeProcessing(escapeProcessing);
        String sql = command;
        if (removeCRs) {
            sql = sql.replaceAll("\r\n", "\n");
        }
        if (stopOnError) {
            hasResults = statement.execute(sql);
            if (throwWarning) {
				//Oracle环境中，创建存储过程，函数等，返回warning而不是抛出异常。这种情况下获取执行中的warning，有就抛出
                // In Oracle, CRATE PROCEDURE, FUNCTION, etc. returns warning
                // instead of throwing exception if there is compilation error.
                SQLWarning warning = statement.getWarnings();
                if (warning != null) {
                    throw warning;
                }
            }
        } else {
            try {
                hasResults = statement.execute(sql);
            } catch (SQLException e) {
                String message = "Error executing: " + command + ".  Cause: " + e;
                printlnError(message);
            }
        }
        printResults(statement, hasResults);
        try {
            statement.close();
        } catch (Exception e) {
            // Ignore to workaround a bug in some connection pools
        }
    }

	//如果有结果，则打印结果集；否则什么都不做
    private void printResults(Statement statement, boolean hasResults) {
        try {
            if (hasResults) {
                ResultSet rs = statement.getResultSet();
                if (rs != null) {
					//获取行元数据
                    ResultSetMetaData md = rs.getMetaData();
                    int cols = md.getColumnCount();
                    for (int i = 0; i < cols; i++) {
						//打印表格头部
                        String name = md.getColumnLabel(i + 1);
                        print(name + "\t");
                    }
                    println("");
                    while (rs.next()) {
						//打印条目的每一列
                        for (int i = 0; i < cols; i++) {
                            String value = rs.getString(i + 1);
                            print(value + "\t");
                        }
                        println("");
                    }
                }
            }
        } catch (SQLException e) {
            printlnError("Error printing results: " + e.getMessage());
        }
    }

    private void print(Object o) {
        if (logWriter != null) {
            logWriter.print(o);
            logWriter.flush();
        }
    }

    //控制台打印日志
    private void println(Object o) {
        if (logWriter != null) {
            logWriter.println(o);
            logWriter.flush();
        }
    }
    //控制台打印错误
    private void printlnError(Object o) {
        if (errorLogWriter != null) {
            errorLogWriter.println(o);
            errorLogWriter.flush();
        }
    }

}
