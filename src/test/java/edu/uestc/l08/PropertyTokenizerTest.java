package edu.uestc.l08;

import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.junit.Test;

/**
 * Created by zhiqianye on 2016/12/30.
 */
public class PropertyTokenizerTest {
    @Test
    public void testMethodToProperty() throws Exception {
        PropertyTokenizer tokenizer = new PropertyTokenizer("阿士大夫[0].千叶.智");
    }
}
