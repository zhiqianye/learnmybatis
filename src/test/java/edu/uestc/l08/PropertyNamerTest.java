package edu.uestc.l08;

import org.apache.ibatis.reflection.property.PropertyNamer;
import org.junit.Test;

/**
 * Created by zhiqianye on 2016/12/30.
 */
public class PropertyNamerTest {
    @Test
    public void testMethodToProperty() throws Exception {
        String whoami = PropertyNamer.methodToProperty("getSomeField");
        System.out.println(whoami);

        boolean isGetter = PropertyNamer.isGetter("getSomeField");
        System.out.println(isGetter);

        isGetter = PropertyNamer.isGetter("isStoped");
        System.out.println(isGetter);

        boolean isSetter = PropertyNamer.isSetter("setSomeField");
        System.out.println(isSetter);

    }
}
