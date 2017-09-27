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
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * @author Clinton Begin
 */

/**
 * 插件拦截器
 * 从Configuration的plugin方法可以得知，可以拦截
 * ParameterHandler、ResultSetHandler、StatementHandler、Executor共4个接口对象内的方法。
 */
public interface Interceptor {

	/**
	 * 执行拦截内容的地方，比如想收点保护费。
	 * 由plugin()方法触发，interceptor.plugin(target)足以证明
	 */
	Object intercept(Invocation invocation) throws Throwable;

	/**
	 * 为Object注册拦截处理，需要注意的是这里是注册，而intercept()才是处理
	 */
	Object plugin(Object target);

	/**
	 * 给自定义的拦截器传递xml配置的属性参数
	 */
	void setProperties(Properties properties);

}
