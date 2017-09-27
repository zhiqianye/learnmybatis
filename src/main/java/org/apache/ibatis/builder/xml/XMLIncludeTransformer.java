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
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;

/**
 * @author Frank D. Martinez [mnesarco]
 */
/**
 * XML include转换器
 *
 */
public class XMLIncludeTransformer {

	private final Configuration configuration;
	private final MapperBuilderAssistant builderAssistant;

	public XMLIncludeTransformer(Configuration configuration, MapperBuilderAssistant builderAssistant) {
		this.configuration = configuration;
		this.builderAssistant = builderAssistant;
	}

	public void applyIncludes(Node source) {
		Properties variablesContext = new Properties();
		Properties configurationVariables = configuration.getVariables();
		if (configurationVariables != null) {
			variablesContext.putAll(configurationVariables);
		}
		applyIncludes(source, variablesContext);
	}

	/**
	 * Recursively apply includes through all SQL fragments.
	 * @param source Include node in DOM tree
	 * @param variablesContext Current context for static variables with values
	 */
	/*
		1.解析节点
		<select id="countAll" resultType="int">
			select count(1) from (
				<include refid="studentProperties"></include>
			) tmp
		</select>

		2.include节点替换为sqlFragment节点
		<select id="countAll" resultType="int">
			select count(1) from (
					<sql id="studentProperties">
						select
							stud_id as studId
							, name, email
							, dob
							, phone
						from students
					</sql>
			) tmp
		</select>

		3.将sqlFragment的子节点（文本节点）insert到sqlFragment节点的前面。对于dom来说，文本也是一个节点，叫TextNode。
		<select id="countAll" resultType="int">
			select count(1) from (
						select
							stud_id as studId
							, name, email
							, dob
							, phone
						from students
					<sql id="studentProperties">
						select
							stud_id as studId
							, name, email
							, dob
							, phone
						from students
					</sql>
			) tmp
		</select>

		4.移除sqlFragment节点
		<select id="countAll" resultType="int">
			select count(1) from (
						select
							stud_id as studId
							, name, email
							, dob
							, phone
						from students
			) tmp
		</select>
		这也是为什么要移除<selectKey> and <include>节点的原因。
	 */
	private void applyIncludes(Node source, final Properties variablesContext) {
		if (source.getNodeName().equals("include")) {
			// new full context for included SQL - contains inherited context and new variables from current include node
			Properties fullContext;

			String refid = getStringAttribute(source, "refid");
			// replace variables in include refid value
			//refid替换为属性列表中的值
			refid = PropertyParser.parse(refid, variablesContext);
			//拿到SQL片段
			Node toInclude = findSqlFragment(refid);
			Properties newVariablesContext = getVariablesContext(source, variablesContext);
			if (!newVariablesContext.isEmpty()) {
				// merge contexts
				fullContext = new Properties();
				fullContext.putAll(variablesContext);
				fullContext.putAll(newVariablesContext);
			} else {
				// no new context - use inherited fully
				fullContext = variablesContext;
			}
			//递归
			applyIncludes(toInclude, fullContext);
			if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
				toInclude = source.getOwnerDocument().importNode(toInclude, true);
			}
			// 将include节点，替换为sqlFragment节点
			source.getParentNode().replaceChild(toInclude, source);
			while (toInclude.hasChildNodes()) {
				//在节点前插入
				toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
			}
			// 移除sqlFragment节点
			toInclude.getParentNode().removeChild(toInclude);
		} else if (source.getNodeType() == Node.ELEMENT_NODE) {
			NodeList children = source.getChildNodes();
			for (int i=0; i<children.getLength(); i++) {
				// 递归调用
				applyIncludes(children.item(i), variablesContext);
			}
		} else if (source.getNodeType() == Node.ATTRIBUTE_NODE && !variablesContext.isEmpty()) {
			// replace variables in all attribute values
			// 通过PropertyParser替换所有${xxx}占位符(attribute属性)
			source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
		} else if (source.getNodeType() == Node.TEXT_NODE && !variablesContext.isEmpty()) {
			// replace variables ins all text nodes
			// 通过PropertyParser替换所有${xxx}占位符(文本节点)
			source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
		}
	}

	private Node findSqlFragment(String refid) {
		refid = builderAssistant.applyCurrentNamespace(refid, true);
		try {
			XNode nodeToInclude = configuration.getSqlFragments().get(refid);
			return nodeToInclude.getNode().cloneNode(true);
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("Could not find SQL statement to include with refid '" + refid + "'", e);
		}
	}

	private String getStringAttribute(Node node, String name) {
		return node.getAttributes().getNamedItem(name).getNodeValue();
	}

	/**
	 * Read placholders and their values from include node definition.
	 * @param node Include node instance
	 * @param inheritedVariablesContext Current context used for replace variables in new variables values
	 * @return variables context from include instance (no inherited values)
	 */
	private Properties getVariablesContext(Node node, Properties inheritedVariablesContext) {
		Properties variablesContext = new Properties();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String name = getStringAttribute(n, "name");
				String value = getStringAttribute(n, "value");
				// Replace variables inside
				value = PropertyParser.parse(value, inheritedVariablesContext);
				// Push new value
				Object originalValue = variablesContext.put(name, value);
				if (originalValue != null) {
					throw new BuilderException("Variable " + name + " defined twice in the same include definition");
				}
			}
		}
		return variablesContext;
	}

}
