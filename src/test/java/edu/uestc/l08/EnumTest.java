package edu.uestc.l08;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by zhiqianye on 2017/1/4.
 */
public class EnumTest {

    @Test
    public void testOrdinal() throws Exception {
        System.out.println(Type.A.ordinal());
        System.out.println(Type.B.ordinal());
        System.out.println(Type.C.ordinal());

    }

    @Test
    public void testEnumConstants() throws Exception {
        Type[] enumConstants = Type.class.getEnumConstants();
        System.out.println(Arrays.toString(enumConstants));

    }

    @Test
    public void testGetName() throws Exception {
        System.out.println(Type.A.name());

    }

    @Test
    public void testValueOf() throws Exception {
        System.out.println(Enum.valueOf(Type.class, "A"));

    }
}
enum Type{
    A,B,C
}
