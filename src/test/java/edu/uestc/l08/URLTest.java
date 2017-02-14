package edu.uestc.l08;

import org.junit.Test;
import sun.misc.Launcher;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zhiqianye on 2016/12/27.
 */
public class URLTest {

    public static void main(String[] args){
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        for (URL url : urls)
            System.out.println(url.toExternalForm());
    }

    @Test
    public void testJarUrl() throws MalformedURLException {
        URL url = new URL("file:/C:/Program%20Files/Java/jdk1.8.0_45/jre/lib/resources.jar");
        System.out.println(url.toExternalForm());

        url = new URL(url.toExternalForm());
        System.out.println(url.toExternalForm());

        try {
            for (;;) {
                url = new URL(url.getFile());
                System.out.println(url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
