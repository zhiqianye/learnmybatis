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

import java.sql.ResultSet;

/**
 * @author Clinton Begin
 */

/**
 * 结果集类型
 */
public enum ResultSetType {
	/**
	 * 默认的cursor类型，仅仅支持向前forward，不支持backforward，random，last，first操作，类似单向链表。
	 * TYPE_FORWARD_ONLY类型通常是效率最高最快的cursor类型
	 */
	FORWARD_ONLY(ResultSet.TYPE_FORWARD_ONLY),
	/**
	 *  支持backforward，random，last，first操作，对其它数据session对选择数据做出的更改是不敏感，不可见的。
	 */
	SCROLL_INSENSITIVE(ResultSet.TYPE_SCROLL_INSENSITIVE),
	/**
	 * 支持backforward，random，last，first操作，对其它数据session对选择数据做出的更改是敏感，可见的。
	 */
	SCROLL_SENSITIVE(ResultSet.TYPE_SCROLL_SENSITIVE);

	private int value;

	ResultSetType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
