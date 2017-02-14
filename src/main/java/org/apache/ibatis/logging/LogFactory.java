package org.apache.ibatis.logging;

import java.lang.reflect.Constructor;

/**
 * @author Clinton Begin
 * @author Eduardo Macarron
 */

/**
 * 日志工厂
 */
public final class LogFactory {

    /**
     * Marker to be used by logging implementations that support markers
     */
    /**
     * 给支持marker功能的logger使用(目前有slf4j, log4j2, logback)
     */
    public static final String MARKER = "MYBATIS";

    /**
     * 接口Log的具体实现类的构造器，实现类包括：
     * SLF4J
     * Apache Commons Logging
     * Log4j2
     * Log4j
     * JDK logging
     * no-logging
     */
    private static Constructor<? extends Log> logConstructor;

    /**
     * 具体选择哪个日志实现工具由MyBatis的内置日志工厂确定。它会使用最先找到的（按上文列举的顺序查找）。 如果一个都未找到，日志功能就会被禁用。
     */
    static {
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useSlf4jLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useCommonsLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useLog4J2Logging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useLog4JLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useJdkLogging();
            }
        });
        tryImplementation(new Runnable() {
            @Override
            public void run() {
                useNoLogging();
            }
        });
    }

    /**
     * 设计上作为工具类，使用工具方法
     */
    private LogFactory() {
        // disable construction
    }

    /**
     * 根据传入的Log实现类来获取日志对象
     * @param aClass 类的Class对象
     * @return 实现类实例
     */
    public static Log getLog(Class<?> aClass) {
        return getLog(aClass.getName());
    }

    /**
     * 根据传入的字符串名称来构造日志实现实例
     * @param logger 日志名
     * @return 实现类实例
     */
    public static Log getLog(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LogException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    /**
     * 扩展方法，支持自定义的Log实现，通过传入实现类Class对象设置日志构造器
     * @param clazz
     */
    public static synchronized void useCustomLogging(Class<? extends Log> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useSlf4jLogging() {
        setImplementation(org.apache.ibatis.logging.slf4j.Slf4jImpl.class);
    }

    public static synchronized void useCommonsLogging() {
        setImplementation(org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class);
    }

    public static synchronized void useLog4JLogging() {
        setImplementation(org.apache.ibatis.logging.log4j.Log4jImpl.class);
    }

    public static synchronized void useLog4J2Logging() {
        setImplementation(org.apache.ibatis.logging.log4j2.Log4j2Impl.class);
    }

    public static synchronized void useJdkLogging() {
        setImplementation(org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class);
    }

    public static synchronized void useStdOutLogging() {
        setImplementation(org.apache.ibatis.logging.stdout.StdOutImpl.class);
    }

    public static synchronized void useNoLogging() {
        setImplementation(org.apache.ibatis.logging.nologging.NoLoggingImpl.class);
    }


    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                //巧妙的设计，通过runnable来实现了一个精简的模板设计模式
                //此处不是多线程并发执行，没有线程对象，且调用的是run方法
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends Log> implClass) {
        try {
            //没有抛出异常，表示找到了对应的实现类，从而通过反射设置对应的构造器
            Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
            Log log = candidate.newInstance(LogFactory.class.getName());
            if (log.isDebugEnabled()) {
                log.debug("Logging initialized using '" + implClass + "' adapter.");
            }
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LogException("Error setting Log implementation.  Cause: " + t, t);
        }
    }

}
