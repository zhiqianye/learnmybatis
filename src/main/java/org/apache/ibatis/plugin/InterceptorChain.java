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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class InterceptorChain {

	private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

	/**
	 * 上面的for循环代表了只要是插件，都会以责任链的方式逐一执行
	 * （别指望它能跳过某个节点），所谓插件，其实就类似于拦截器。
	 */
	public Object pluginAll(Object target) {
		for (Interceptor interceptor : interceptors) {
			target = interceptor.plugin(target);
		}
		return target;
	}

	public void addInterceptor(Interceptor interceptor) {
		interceptors.add(interceptor);
	}

	public List<Interceptor> getInterceptors() {
		return Collections.unmodifiableList(interceptors);
	}

}
