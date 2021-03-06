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
package org.apache.ibatis.executor.keygen;

import java.sql.Statement;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @author Clinton Begin
 */

/**
 * 键值生成器
 * 注意：KeyGenerator的作用，是返回数据库生成的自增主键值，而不是生成数据库的自增主键值。
 * 返回的主键值放到parameter object的主键属性上。
 * 基于策略处理
 */
public interface KeyGenerator {

	//定了2个回调方法，processBefore,processAfter

	//执行Statement之前调用
	void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter);
	//执行Statement之后调用
	void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter);

}
