package edu.uestc.l08;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhiqianye on 2017/7/13.
 */
public class MapperProxy implements InvocationHandler {

	@SuppressWarnings("unchecked")
	public <T> T newInstance(Class<T> clz) {
		return (T) Proxy.newProxyInstance(clz.getClassLoader(), new Class[] { clz }, this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) {
			try {
				// 诸如hashCode()、toString()、equals()等方法，将target指向当前对象this
				return method.invoke(this, args);
			} catch (Throwable t) {
			}
		}
		// 投鞭断流
		return new User((Integer) args[0], "zhangsan", 18);
	}

	public static void main(String[] args) {
		MapperProxy proxy = new MapperProxy();

		UserMapper mapper = proxy.newInstance(UserMapper.class);
		User user = mapper.getUserById(1001);

		System.out.println("ID:" + user.getId());
		System.out.println("Name:" + user.getName());
		System.out.println("Age:" + user.getAge());

		System.out.println(mapper.toString());
	}
}