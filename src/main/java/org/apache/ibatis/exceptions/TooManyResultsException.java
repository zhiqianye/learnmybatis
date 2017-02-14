package org.apache.ibatis.exceptions;

/**
 * @author Clinton Begin
 */

/**
 * 结果太多异常。持久化异常子类，语义异常。通常select语义预期返回一条结果，而实际返回了多于一条结果时抛出该异常。
 */
public class TooManyResultsException extends PersistenceException {

    private static final long serialVersionUID = 8935197089745865786L;

    public TooManyResultsException() {
        super();
    }

    public TooManyResultsException(String message) {
        super(message);
    }

    public TooManyResultsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyResultsException(Throwable cause) {
        super(cause);
    }
}
