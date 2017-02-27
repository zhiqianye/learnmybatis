package org.apache.ibatis.io;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by zhiqianye on 2017/2/26.
 */
public class DefaultVFS extends VFS{
    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    protected List<String> list(URL url, String forPath) throws IOException {
        return null;
    }
}
