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
package org.apache.ibatis.scripting.xmltags;

/**
 * @author Frank D. Martinez [mnesarco]
 */

/**
 * 处理动态sql标签<bind>的SqlNode类
 */
public class VarDeclSqlNode implements SqlNode {

	private final String name;
	private final String expression;

	public VarDeclSqlNode(String var, String exp) {
		name = var;
		expression = exp;
	}

	@Override
	public boolean apply(DynamicContext context) {
		final Object value = OgnlCache.getValue(expression, context.getBindings());
		// 由于没有sql可append，仅是把bind标签的变量名和值保存至上下文参数列表内
		context.bind(name, value);
		return true;
	}

}
