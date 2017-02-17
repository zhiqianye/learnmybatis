package edu.uestc.l08;

import org.junit.Test;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Created by zhiqianye on 2016/12/28.
 */
public class ReflectTest {
    @Test
    public void testConstructor() throws Exception {
       /* Constructor<Foo> fc = Foo.class.getDeclaredConstructor(int.class);
        System.out.println(fc.isAccessible());

        Constructor<Bar> bc = Bar.class.getDeclaredConstructor(int.class);
        System.out.println(bc.isAccessible());*/

        Class<? super Concrete2> superclass = Concrete2.class.getSuperclass();
        System.out.println(superclass);
        System.out.println(superclass instanceof Class);

        Class<? super Object> objectSuper = Object.class.getSuperclass();
        System.out.println(objectSuper);


        java.lang.reflect.Type type = Concrete1.class.getGenericSuperclass();
        System.out.println(type.getTypeName());
        System.out.println(type instanceof Class);

        Field value = Base.class.getDeclaredField("value");
        System.out.println(value);
        Base base = new Base();
        value.setAccessible(true);
        value.set(base, 10);
        System.out.println(base.getValue());

        I1 mf = new MultiInterface();
        System.out.println(mf instanceof I2);

    }

    private static class Foo{
        public Foo() {
        }

        public Foo(int i) {
        }
    }
}

class Bar{
    public Bar() {
    }

    public Bar(int i) {
    }
}

class Base{
    private int value = 404;

    public int getValue() {
        return value;
    }
}

class Concrete1 extends Base{

}

class Concrete2 extends Concrete1{

}

interface I1{

}

interface I2{

}

class MultiInterface implements I1, I2{

}


