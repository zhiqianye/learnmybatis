package org.apache.ibatis.logging;

/**
 * @author Clinton Begin
 */

/**
 * 日志接口，抽象MyBatis所需的基本的debug，warn，error等日志记录方法。
 * 现有的日志框架有很多，这种设计对下屏蔽不同实现细节，对上提供统一接口。是一种适配器模式的应用场景。
 * 但是slf4j就实现了这一目的，这里为什么重复造轮子？
 */
public interface Log {

    boolean isDebugEnabled();

    boolean isTraceEnabled();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);

}
