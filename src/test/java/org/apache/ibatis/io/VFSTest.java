package org.apache.ibatis.io;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by zhiqianye on 2017/2/26.
 */
public class VFSTest {

    @Test
    public void testGetResourse() throws IOException {
        System.out.println(getClass().getResource("/"));
        System.out.println(getClass().getResource(""));
    }
}
