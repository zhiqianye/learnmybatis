/**
 *    Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.scripting;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

/**
 * 脚本语言驱动
 *
 */
public interface LanguageDriver {

	/**
	 * 创建参数处理器
	 *
	 * @param mappedStatement 被执行的映射Statement
	 * @param parameterObject 输入参数
	 * @param boundSql 动态sql解析的结果
	 * @return
	 * @author Frank D. Martinez [mnesarco]
	 * @see DefaultParameterHandler
	 */
	ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

	/**
	 * 从xml文件中读取的，持有statement的SqlSource
	 * 当映射Statement被从xml文件或者class中读取时触发
	 *
	 * @param configuration MyBatis配置对象
	 * @param script xml文件的XNode对象
	 * @param parameterType 输入参数类型
	 * @return
	 */
	SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);

	/**
	 * 从注解中中读取的，持有statement的SqlSource
	 * 当映射Statement被从xml文件或者class中读取时触发
	 *
	 * @param configuration MyBatis配置对象
	 * @param script 注解内容
	 * @param parameterType 输入参数类型
	 * @return
	 */
	SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

}
