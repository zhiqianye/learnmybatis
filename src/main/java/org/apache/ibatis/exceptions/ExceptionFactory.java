package org.apache.ibatis.exceptions;

import org.apache.ibatis.executor.ErrorContext;

/**
 * @author Clinton Begin
 */

/**
 * 异常工厂。提供异常相关工具方法。
 */
public class ExceptionFactory {

    private ExceptionFactory() {
        // Prevent Instantiation
    }

    /**
     * 把字符串message和异常对象e封装成自定义的语义异常。
     * 通常用于将checked异常转化为unchecked异常
     * @param message 字符串消息对象
     * @param e 异常对消
     * @return 封装成的语义异常对象
     */
    public static RuntimeException wrapException(String message, Exception e) {
        return new PersistenceException(ErrorContext.instance().message(message).cause(e).toString(), e);
    }

}