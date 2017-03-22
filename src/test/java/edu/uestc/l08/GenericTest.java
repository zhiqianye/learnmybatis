package edu.uestc.l08;

import java.util.HashMap;
import java.util.Map;

public class GenericTest<T1,T2 extends Number> {
    private Map<T1 , Integer> map = null;

    private Map<? extends Number, ? super Integer> map1 = new HashMap<Integer,Integer>();

    private T1[] tArray = null;
}