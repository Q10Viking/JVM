package org.hzz.classLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 模拟Tomcat自定义类加载器
 */
public class MyClassLoader3 extends ClassLoader{
    private String classpath;
    public MyClassLoader3(String classpath){ this.classpath = classpath; }

    private byte[] loadData(String name) throws IOException {
        //  注意点号的转义
        name = name.replaceAll("\\.","/");
        String path = classpath+"/"+name+".class";
        try(FileInputStream in = new FileInputStream(path)){
            int available = in.available();
            byte[] res = new byte[available];
            in.read(res,0,available);
            return res;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes;
        try {
            bytes = loadData(name);
            return defineClass(name,bytes,0,bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException();
        }
    }

    @Override
    public Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    //  打破双亲委派
                    if(name.startsWith("org.hzz")){
                        c = findClass(name);
                    }else{
                        c = getParent().loadClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }

    public static void main(String[] args) throws Exception {
        //  同一个JVM内，两个相同包名和类名的类对象可以共存，因为他们的类加载器可以不一样，从而实现独立部署
        MyClassLoader3 myClassLoader1 = new MyClassLoader3("D:/Code/tuling/test");
        Class<?> clazz1 = myClassLoader1.loadClass("org.hzz.Info");

        MyClassLoader3 myClassLoader2 = new MyClassLoader3("D:/Code/tuling/test");
        Class<?> clazz2 = myClassLoader2.loadClass("org.hzz.Info");
        Class<?> clazz3 = myClassLoader2.loadClass("org.hzz.Info");

        System.out.println(clazz1==clazz2);
        System.out.println(clazz3==clazz2); //  同一个类加载器
    }
}
/**
 * false
 * true
 */