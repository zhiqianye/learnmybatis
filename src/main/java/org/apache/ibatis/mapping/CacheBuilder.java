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
package org.apache.ibatis.mapping;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheException;
import org.apache.ibatis.cache.decorators.BlockingCache;
import org.apache.ibatis.cache.decorators.LoggingCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.ScheduledCache;
import org.apache.ibatis.cache.decorators.SerializedCache;
import org.apache.ibatis.cache.decorators.SynchronizedCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * @author Clinton Begin
 */
/**
 * 缓存构建器,建造者模式
 *
 */
public class CacheBuilder {
	//缓存ID
	private String id;
	//缓存实现类
	private Class<? extends Cache> implementation;
	//缓存装饰器
	private List<Class<? extends Cache>> decorators;
	//缓存大小
	private Integer size;
	//清理间隔
	private Long clearInterval;
	//可读写
	private boolean readWrite;
	//属性
	private Properties properties;
	//阻塞？
	private boolean blocking;

	public CacheBuilder(String id) {
		this.id = id;
		this.decorators = new ArrayList<Class<? extends Cache>>();
	}

	public CacheBuilder implementation(Class<? extends Cache> implementation) {
		this.implementation = implementation;
		return this;
	}

	public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
		if (decorator != null) {
			this.decorators.add(decorator);
		}
		return this;
	}

	public CacheBuilder size(Integer size) {
		this.size = size;
		return this;
	}

	public CacheBuilder clearInterval(Long clearInterval) {
		this.clearInterval = clearInterval;
		return this;
	}

	public CacheBuilder readWrite(boolean readWrite) {
		this.readWrite = readWrite;
		return this;
	}

	public CacheBuilder blocking(boolean blocking) {
		this.blocking = blocking;
		return this;
	}

	public CacheBuilder properties(Properties properties) {
		this.properties = properties;
		return this;
	}

	public Cache build() {
		//设置默认实现
		setDefaultImplementations();
		//获取默认实现的实例
		Cache cache = newBaseCacheInstance(implementation, id);
		//为实例设置属性
		setCacheProperties(cache);
		// issue #352, do not apply decorators to custom caches
		if (PerpetualCache.class.equals(cache.getClass())) {
			//如果是系统提供的默认实现，则进行包装
			for (Class<? extends Cache> decorator : decorators) {
				cache = newCacheDecoratorInstance(decorator, cache);
				setCacheProperties(cache);
			}
			cache = setStandardDecorators(cache);
		} else if (!LoggingCache.class.isAssignableFrom(cache.getClass())) {
			//如果是日志cache的资料，则仅包装日志
			cache = new LoggingCache(cache);
		}
		return cache;
	}

	//设置默认的缓存实现，和默认的装饰器
	private void setDefaultImplementations() {
		if (implementation == null) {
			implementation = PerpetualCache.class;
			if (decorators.isEmpty()) {
				decorators.add(LruCache.class);
			}
		}
	}

	//为缓存实现设置装饰器，注意，缓存可多级包装
	private Cache setStandardDecorators(Cache cache) {
		try {
			MetaObject metaCache = SystemMetaObject.forObject(cache);
			if (size != null && metaCache.hasSetter("size")) {
				metaCache.setValue("size", size);
			}
			if (clearInterval != null) {
				cache = new ScheduledCache(cache);
				((ScheduledCache) cache).setClearInterval(clearInterval);
			}
			if (readWrite) {
				cache = new SerializedCache(cache);
			}
			cache = new LoggingCache(cache);
			cache = new SynchronizedCache(cache);
			if (blocking) {
				cache = new BlockingCache(cache);
			}
			return cache;
		} catch (Exception e) {
			throw new CacheException("Error building standard cache decorators.  Cause: " + e, e);
		}
	}

	private void setCacheProperties(Cache cache) {
		if (properties != null) {
			//可以认为是MetaObject的一个应用实例
			MetaObject metaCache = SystemMetaObject.forObject(cache);
			//属性逐条设置
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String name = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (metaCache.hasSetter(name)) {
					//如果缓存有对应的设置器，则获取实际的设置参数类型
					Class<?> type = metaCache.getSetterType(name);
					//针对不同类型进行设置
					if (String.class == type) {
						metaCache.setValue(name, value);
					} else if (int.class == type
							|| Integer.class == type) {
						metaCache.setValue(name, Integer.valueOf(value));
					} else if (long.class == type
							|| Long.class == type) {
						metaCache.setValue(name, Long.valueOf(value));
					} else if (short.class == type
							|| Short.class == type) {
						metaCache.setValue(name, Short.valueOf(value));
					} else if (byte.class == type
							|| Byte.class == type) {
						metaCache.setValue(name, Byte.valueOf(value));
					} else if (float.class == type
							|| Float.class == type) {
						metaCache.setValue(name, Float.valueOf(value));
					} else if (boolean.class == type
							|| Boolean.class == type) {
						metaCache.setValue(name, Boolean.valueOf(value));
					} else if (double.class == type
							|| Double.class == type) {
						metaCache.setValue(name, Double.valueOf(value));
					} else {
						throw new CacheException("Unsupported property type for cache: '" + name + "' of type " + type);
					}
				}
			}
		}
	}

	//创建缓存实现的实例
	private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
		Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);
		try {
			return cacheConstructor.newInstance(id);
		} catch (Exception e) {
			throw new CacheException("Could not instantiate cache implementation (" + cacheClass + "). Cause: " + e, e);
		}
	}

	//获取缓存实现的构造器
	private Constructor<? extends Cache> getBaseCacheConstructor(Class<? extends Cache> cacheClass) {
		try {
			return cacheClass.getConstructor(String.class);
		} catch (Exception e) {
			throw new CacheException("Invalid base cache implementation (" + cacheClass + ").  " +
					"Base cache implementations must have a constructor that takes a String id as a parameter.  Cause: " + e, e);
		}
	}

	//创建装饰器实例
	private Cache newCacheDecoratorInstance(Class<? extends Cache> cacheClass, Cache base) {
		Constructor<? extends Cache> cacheConstructor = getCacheDecoratorConstructor(cacheClass);
		try {
			return cacheConstructor.newInstance(base);
		} catch (Exception e) {
			throw new CacheException("Could not instantiate cache decorator (" + cacheClass + "). Cause: " + e, e);
		}
	}

	//获取装饰器的构造器
	private Constructor<? extends Cache> getCacheDecoratorConstructor(Class<? extends Cache> cacheClass) {
		try {
			return cacheClass.getConstructor(Cache.class);
		} catch (Exception e) {
			throw new CacheException("Invalid cache decorator (" + cacheClass + ").  " +
					"Cache decorators must have a constructor that takes a Cache instance as a parameter.  Cause: " + e, e);
		}
	}
}
