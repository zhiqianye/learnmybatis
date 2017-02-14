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
package org.apache.ibatis.parsing;

/**
 * @author Clinton Begin
 */

/**
 * 通用的token处理类，主要用于处理mybatis的xml文件中的#{arg}和${arg}的情况
 */
public class GenericTokenParser {
    //参数开始标记，如${
    private final String openToken;
    //参数结束标记，如}
    private final String closeToken;
    //token处理器
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text) {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder expression = new StringBuilder();
        if (text != null && text.length() > 0) {
            char[] src = text.toCharArray();
            int offset = 0;
            // 查找开始标记
            int start = text.indexOf(openToken, offset);
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    //如果找到开始标记，但是被转义了，则保持语义，仅删掉反斜线并继续
                    builder.append(src, offset, start - offset - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    //如果找到开始标记，且未被被转义，则开始查找结束标记
                    expression.setLength(0);
                    builder.append(src, offset, start - offset);
                    offset = start + openToken.length();
                    int end = text.indexOf(closeToken, offset);
                    while (end > -1) {
                        if (end > offset && src[end - 1] == '\\') {
                            // 找到结束标记被转义，则删掉反斜线并继续
                            expression.append(src, offset, end - offset - 1).append(closeToken);
                            offset = end + closeToken.length();
                            end = text.indexOf(closeToken, offset);
                        } else {
                            // 找到结束标记，则获取表达式key
                            expression.append(src, offset, end - offset);
                            offset = end + closeToken.length();
                            break;
                        }
                    }
                    if (end == -1) {
                        //没找到结束标记，表示没有遇到表达式变量
                        builder.append(src, start, src.length - start);
                        offset = src.length;
                    } else {
                        //找到结束标记，则将表达式变量替换成真实的属性值
                        builder.append(handler.handleToken(expression.toString()));
                        offset = end + closeToken.length();
                    }
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                //追加剩余字符串
                builder.append(src, offset, src.length - offset);
            }
        }
        return builder.toString();
    }
}
