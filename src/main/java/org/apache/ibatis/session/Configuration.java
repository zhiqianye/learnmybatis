package org.apache.ibatis.session;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Map;

public class Configuration {
    protected boolean useActualParamName = true;
    private TypeHandlerRegistry typeHandlerRegistry;
    private ObjectFactory objectFactory;
    private ObjectWrapperFactory objectWrapperFactory;
    private ReflectorFactory reflectorFactory;
    private Environment environment;
    private ExecutorType defaultExecutorType;
	private boolean lazyLoadingEnabled;
	private boolean useGeneratedKeys;
	private String logPrefix;
	private LanguageDriver defaultScriptingLanuageInstance;
	private TypeAliasRegistry typeAliasRegistry;

	public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return null;
    }

    public MetaObject newMetaObject(Map<String, Object> additionalParameters) {
        return null;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public MappedStatement getMappedStatement(String statement) {
        return null;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Executor newExecutor(Transaction tx, ExecutorType execType) {
        return null;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

	public boolean isLazyLoadingEnabled() {
		return lazyLoadingEnabled;
	}

	public ResultMap getResultMap(String rmId) {
		return null;
	}

	public boolean isUseGeneratedKeys() {
		return useGeneratedKeys;
	}

	public String getLogPrefix() {
		return logPrefix;
	}

	public LanguageDriver getDefaultScriptingLanuageInstance() {
		return defaultScriptingLanuageInstance;
	}

	public MetaObject newMetaObject(Object parameter) {
		return null;
	}

	public TypeAliasRegistry getTypeAliasRegistry() {
		return typeAliasRegistry;
	}

	public LanguageDriverRegistry getLanguageRegistry() {
		return null;
	}

	public Cache getCache(String namespace) {
		return null;
	}

	public void addCache(Cache cache) {

	}

	public void addParameterMap(ParameterMap parameterMap) {

	}

	public void addResultMap(ResultMap resultMap) {

	}

	public boolean hasResultMap(String extend) {
		return false;
	}

	public void addMappedStatement(MappedStatement statement) {

	}

	public ParameterMap getParameterMap(String parameterMapName) {
		return null;
	}
}
