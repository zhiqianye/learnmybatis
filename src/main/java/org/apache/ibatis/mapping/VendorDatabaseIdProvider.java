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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.executor.BaseExecutor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * Vendor DatabaseId provider
 *
 * It returns database product name as a databaseId
 * If the user provides a properties it uses it to translate database product name
 * key="Microsoft SQL Server", value="ms" will return "ms"
 * It can return null, if no database product name or
 * a properties was specified and no translation was found
 *
 * @author Eduardo Macarron
 */
/**
 * 厂商数据库Id提供者
 */
public class VendorDatabaseIdProvider implements DatabaseIdProvider {

	private static final Log log = LogFactory.getLog(BaseExecutor.class);

	private Properties properties;

	@Override
	public String getDatabaseId(DataSource dataSource) {
		if (dataSource == null) {
			throw new NullPointerException("dataSource cannot be null");
		}
		try {
			return getDatabaseName(dataSource);
		} catch (Exception e) {
			log.error("Could not get a databaseId from dataSource", e);
		}
		return null;
	}

	@Override
	public void setProperties(Properties p) {
		this.properties = p;
	}

	private String getDatabaseName(DataSource dataSource) throws SQLException {
		String productName = getDatabaseProductName(dataSource);
		if (this.properties != null) {
			// 如果设置了属性，则从属性中获取
			// 从属性中寻找产品名对应的配置值，如果有就返回，如果没有就返回null
			for (Map.Entry<Object, Object> property : properties.entrySet()) {
				if (productName.contains((String) property.getKey())) {
					return (String) property.getValue();
				}
			}
			// no match, return null
			return null;
		}
		return productName;
	}

	//获取数据库产品的名字
	private String getDatabaseProductName(DataSource dataSource) throws SQLException {
		Connection con = null;
		try {
			//逻辑是通过数据源拿到连接，从连接拿到元数据，从元数据拿到数据库产品名字
			con = dataSource.getConnection();
			DatabaseMetaData metaData = con.getMetaData();
			return metaData.getDatabaseProductName();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// ignored
				}
			}
		}
	}

}
