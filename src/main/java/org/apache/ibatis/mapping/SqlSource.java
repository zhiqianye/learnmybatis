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

/**
 * Represents the content of a mapped statement read from an XML file or an annotation.
 * It creates the SQL that will be passed to the database out of the input parameter received from the user.
 *
 * @author Clinton Begin
 */

/**
 * 代表从xml文件中或者注释中读到的源SQL
 * 它用来接收来自用户的参数输入，并创建数据库可执行的SQL。
 * 本接口共有4个实现：
 * DynamicSqlSource:处理动态sql。
 * RawSqlSource：处理静态sql，其内部装饰StaticSqlSource。
 * StaticSqlSource：处理静态sql，无论是静态sql，还是动态sql，最终的处理结果，都是静态sql。
 * ProviderSqlSource:处理注解Annotation形式的sql。
 */
public interface SqlSource {

	BoundSql getBoundSql(Object parameterObject);

}
