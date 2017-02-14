package org.apache.ibatis.exceptions;

/**
 * @author Clinton Begin
 */

/**
 * 持久化异常。继承自框架顶级运行时异常IbatisException，基于语义的异常。
 */
@SuppressWarnings("deprecation")
public class PersistenceException extends IbatisException {

    private static final long serialVersionUID = -7537395265357977271L;

    public PersistenceException() {
        super();
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
