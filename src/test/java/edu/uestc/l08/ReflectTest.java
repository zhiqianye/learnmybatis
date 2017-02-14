package edu.uestc.l08;

import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Created by zhiqianye on 2016/12/28.
 */
public class ReflectTest {
    @Test
    public void testConstructor() throws Exception {
        Constructor<Foo> fc = Foo.class.getDeclaredConstructor(int.class);
        System.out.println(fc.isAccessible());

        Constructor<Bar> bc = Bar.class.getDeclaredConstructor(int.class);
        System.out.println(bc.isAccessible());

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

