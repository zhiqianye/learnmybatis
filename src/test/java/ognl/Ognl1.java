package ognl;

import java.util.HashMap;
import java.util.Map;

/**
 * test1：
 * OGNL会根据表达式从根对象（root）中提取值。
 * test2：
 * 对于使用上下文的OGNL，若不指定从哪一个对象中查找"name"属性，则OGNL直接从根对象(root)查找，
 * 若指定查找对象(使用'#'号指定，如#person1),则从指定的对象中查找，
 * 若指定对象不在上下文中则会抛出异常，换句话说就是是#person1.name形式指定查找对象则必须要保证指定对象在上下文环境中。
 */
public class Ognl1 {
	public static void main(String[] args) {
//test1
//		/* 创建一个Person对象 */
//		Person person = new Person();
//		person.setName("zhangsan");
//
//		try {
//            /* 从person对象中获取name属性的值 */
//			Object value = Ognl.getValue("name", person);
//
//			System.out.println(value);
//		} catch (OgnlException e) {
//			e.printStackTrace();
//		}

//test2
		 /* 创建一个上下文Context对象，它是用保存多个对象一个环境 对象 */
		Map<String, Object> context = new HashMap<String, Object>();

		Person person1 = new Person();
		person1.setName("zhangsan");

		Person person2 = new Person();
		person2.setName("lisi");

		Person person3 = new Person();
		person3.setName("wangwu");

        /* person4不放入到上下文环境中 */
		Person person4 = new Person();
		person4.setName("zhaoliu");

        /* 将person1、person2、person3添加到环境中（上下文中） */
		context.put("person1", person1);
		context.put("person2", person2);
		context.put("person3", person3);

		try {
			/* 获取根对象的"name"属性值 */
			Object value = Ognl.getValue("name", context, person2);
			System.out.println("ognl expression \"name\" evaluation is : " + value);

            /* 获取根对象的"name"属性值 */
			Object value2 = Ognl.getValue("#person2.name", context, person2);
			System.out.println("ognl expression \"#person2.name\" evaluation is : " + value2);

            /* 获取person1对象的"name"属性值 */
			Object value3 = Ognl.getValue("#person1.name", context, person2);
			System.out.println("ognl expression \"#person1.name\" evaluation is : " + value3);

            /* 将person4指定为root对象，获取person4对象的"name"属性，注意person4对象不在上下文中 */
			Object value4 = Ognl.getValue("name", context, person4);
			System.out.println("ognl expression \"name\" evaluation is : " + value4);

            /* 将person4指定为root对象，获取person4对象的"name"属性，注意person4对象不在上下文中 */
			Object value5 = Ognl.getValue("#person4.name", context, person4);
			System.out.println("ognl expression \"person4.name\" evaluation is : " + value5);

            /* 获取person4对象的"name"属性，注意person4对象不在上下文中 */
			// Object value6 = Ognl.getValue("#person4.name", context, person2);
			// System.out.println("ognl expression \"#person4.name\" evaluation is : " + value6);

		} catch (OgnlException e) {
			e.printStackTrace();
		}
	}
}

class Person {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}