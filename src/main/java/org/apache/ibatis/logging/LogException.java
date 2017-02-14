package org.apache.ibatis.logging;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author Clinton Begin
 */

/**
 * 日志异常。继承自PersistenceException，基于语义的异常。
 */
public class LogException extends PersistenceException {

    private static final long serialVersionUID = 1022924004852350942L;

    public LogException() {
        super();
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }

}