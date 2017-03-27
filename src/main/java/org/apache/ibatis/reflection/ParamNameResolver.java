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

package org.apache.ibatis.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 参数名解析器，封装一个方法，并提供方法参数名的支持
 */
public class ParamNameResolver {
    //通用命名前缀
    private static final String GENERIC_NAME_PREFIX = "param";
    //参数类
    private static final String PARAMETER_CLASS = "java.lang.reflect.Parameter";
    //获取方法参数名的方法
    private static Method GET_NAME = null;
    //获取方法参数的方法
    private static Method GET_PARAMS = null;

    static {
        try {
            Class<?> paramClass = Resources.classForName(PARAMETER_CLASS);
            GET_NAME = paramClass.getMethod("getName");
            GET_PARAMS = Method.class.getMethod("getParameters");
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * <p>
     * The key is the index and the value is the name of the parameter.<br />
     * The name is obtained from {@link Param} if specified. When {@link Param} is not specified,
     * the parameter index is used. Note that this index could be different from the actual index
     * when the method has special parameters (i.e. {@link RowBounds} or {@link ResultHandler}).
     * </p>
     * <ul>
     * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
     * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
     * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
     * </ul>
     */
    /**
     * 参数索引-参数名
     * 如果参数被Param注解，则参数名为Param的值，例如
     * aMethod(@Param("M") int a, @Param("N") int b) -> {{0, "M"}, {1, "N"}}
     *
     * 否则参数名为参数索引号，例如
     * aMethod(int a, int b) -> {{0, "0"}, {1, "1"}}
     *
     * 另外，如果参数名为特殊参数，则不存储特殊参数结构
     * aMethod(int a, RowBounds rb, int b) -> {{0, "0"}, {2, "1"}}
     *
     */
    private final SortedMap<Integer, String> names;

    private boolean hasParamAnnotation;

    public ParamNameResolver(Configuration config, Method method) {
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final SortedMap<Integer, String> map = new TreeMap<Integer, String>();
        int paramCount = paramAnnotations.length;
        // get names from @Param annotations
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            if (isSpecialParameter(paramTypes[paramIndex])) {
                // skip special parameters
                //跳过特殊参数的处理
                continue;
            }
            String name = null;
            for (Annotation annotation : paramAnnotations[paramIndex]) {
                if (annotation instanceof Param) {
                    hasParamAnnotation = true;
                    name = ((Param) annotation).value();
                    break;
                }
            }
            if (name == null) {
                // @Param was not specified.
                if (config.isUseActualParamName()) {
                    //这种情况下，实际参数名为arg0，arg1等
                    name = getActualParamName(method, paramIndex);
                }
                if (name == null) {
                    //这种情况下，参数名即为索引0，1等
                    // use the parameter index as the name ("0", "1", ...)
                    // gcode issue #71
                    name = String.valueOf(map.size());
                }
            }
            map.put(paramIndex, name);
        }
        names = Collections.unmodifiableSortedMap(map);
    }

    //获取实际参数名，经过测试，名字叫做arg0，arg1等等
    private String getActualParamName(Method method, int paramIndex) {
        if (GET_PARAMS == null) {
            return null;
        }
        try {
            //先获取各个参数
            Object[] params = (Object[]) GET_PARAMS.invoke(method);
            //在获取索引为paramIndex的参数名称
            return (String) GET_NAME.invoke(params[paramIndex]);
        } catch (Exception e) {
            throw new ReflectionException("Error occurred when invoking Method#getParameters().", e);
        }
    }

    //是否为特殊参数，如果是RowBounds（及其子类）或者ResultHandler（及其子类）时，则为特殊参数
    private static boolean isSpecialParameter(Class<?> clazz) {
        return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
    }

    /**
     * Returns parameter names referenced by SQL providers.
     */
    /**
     * 返回参数名
     */
    public String[] getNames() {
        return names.values().toArray(new String[0]);
    }

    /**
     * <p>
     * A single non-special parameter is returned without a name.<br />
     * Multiple parameters are named using the naming rule.<br />
     * In addition to the default names, this method also adds the generic names (param1, param2,
     * ...).
     * </p>
     */
    //TODO 返回[参数名-索引]格式，其中参数名包括已解析名和默认paramX的名称，值冗余
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            return null;
        } else if (!hasParamAnnotation && paramCount == 1) {
            return args[names.firstKey()];
        } else {
            final Map<String, Object> param = new ParamMap<Object>();
            int i = 0;
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                param.put(entry.getValue(), args[entry.getKey()]);
                // add generic param names (param1, param2, ...)
                final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
                // ensure not to overwrite parameter named with @Param
                if (!names.containsValue(genericParamName)) {
                    param.put(genericParamName, args[entry.getKey()]);
                }
                i++;
            }
            return param;
        }
    }
}
