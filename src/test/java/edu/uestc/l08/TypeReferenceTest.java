package edu.uestc.l08;

import org.apache.ibatis.type.TypeReference;
import org.junit.Test;

import java.net.URL;

/**
 * Created by zhiqianye on 2017/1/3.
 */
public class TypeReferenceTest {
    @Test
    public void testConstructor() throws Exception {

        TypeReference reference = new A<URL>();

    }
}

class A<T> extends TypeReference<T>{

}