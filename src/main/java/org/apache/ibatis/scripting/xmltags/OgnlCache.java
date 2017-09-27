/**
 * Copyright 2009-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.scripting.xmltags;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ognl.Ognl;
import ognl.OgnlException;

import org.apache.ibatis.builder.BuilderException;

/**
 * Caches OGNL parsed expressions.
 *
 * @author Eduardo Macarron
 *
 * @see <a href='http://code.google.com/p/mybatis/issues/detail?id=342'>Issue 342</a>
 */

/**
 * OGNL缓存,根据以上链接，大致是说ognl有性能问题，所以加了一个缓存
 */
public final class OgnlCache {

	private static final Map<String, Object> expressionCache = new ConcurrentHashMap<String, Object>();

	private OgnlCache() {
		// Prevent Instantiation of Static Class
	}

	public static Object getValue(String expression, Object root) {
		try {
			Map<Object, OgnlClassResolver> context = Ognl.createDefaultContext(root, new OgnlClassResolver());
			return Ognl.getValue(parseExpression(expression), context, root);
		} catch (OgnlException e) {
			throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
		}
	}

	private static Object parseExpression(String expression) throws OgnlException {
		Object node = expressionCache.get(expression);
		if (node == null) {
			node = Ognl.parseExpression(expression);
			expressionCache.put(expression, node);
		}
		return node;
	}

}
