package edu.uestc.l08;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by zhiqianye on 2017/1/4.
 */
public class EnumTest {

    @Test
    public void testOrdinal() throws Exception {
        System.out.println(EType.A.ordinal());
        System.out.println(EType.B.ordinal());
        System.out.println(EType.C.ordinal());

    }

    @Test
    public void testEnumConstants() throws Exception {
        EType[] enumConstants = EType.class.getEnumConstants();
        System.out.println(Arrays.toString(enumConstants));

    }

    @Test
    public void testGetName() throws Exception {
        System.out.println(EType.A.name());

    }

    @Test
    public void testValueOf() throws Exception {
        System.out.println(Enum.valueOf(EType.class, "A"));

    }
}
enum EType {
    A,B,C
}
