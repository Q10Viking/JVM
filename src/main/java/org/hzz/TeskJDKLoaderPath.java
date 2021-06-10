package org.hzz;

import sun.misc.Launcher;
import sun.misc.URLClassPath;

import java.net.URL;

public class TeskJDKLoaderPath {
    public static void main(String[] args) {
        System.out.println("bootstrapLoader加载以下文件：");
        //  底层是从这个系统配置信息获取的
        //  String bootClassPath = System.getProperty("sun.boot.class.path");
        URLClassPath bootstrapClassPath = Launcher.getBootstrapClassPath();
        URL[] urls = bootstrapClassPath.getURLs();
        for(URL url:urls){
            System.out.println(url);
        }
        System.out.println("\nextClassloader加载以下文件：");
        System.out.println(System.getProperty("java.ext.dirs"));

        System.out.println("\nappClassLoader加载以下文件：");
        System.out.println(System.getProperty("java.class.path"));
    }
}
/**
 * bootstrapLoader加载以下文件：
 * file:/S:/DevelopEnv/Java/openjdk/jre/lib/rt.jar
 * ...省略其他路径...
 *
 * extClassloader加载以下文件：
 * S:\DevelopEnv\Java\openjdk\jre\lib\ext;C:\Windows\Sun\Java\lib\ext
 *
 * appClassLoader加载以下文件：
 * D:\Code\tuling\JVM\target\classes;
 */