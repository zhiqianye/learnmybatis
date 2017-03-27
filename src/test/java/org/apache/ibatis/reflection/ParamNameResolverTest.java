package org.apache.ibatis.reflection;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.Test;

/**
 * Created by zhiqianye on 2017/3/24.
 */
public class ParamNameResolverTest {
    @Test
    public void test() throws Exception {
        ParamNameResolver test = new ParamNameResolver(new Configuration(), TestParam.class.getMethod("noParam", int.class, RowBounds.class, int.class));

    }


}

class TestParam{

    public void hasParam(@Param("12") int i, @Param("") int j){

    }

    public void noParam(int i, @Param("rb")RowBounds rb, int j){

    }
}
