package org.hzz.classLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 打破双亲委派时出现的沙箱机制保护
 */
public class MyClassLoader2 extends ClassLoader{
    private String classpath;
    public MyClassLoader2(String classpath){ this.classpath = classpath; }

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
                    c = findClass(name);
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
        MyClassLoader2 myClassLoader = new MyClassLoader2("D:/Code/tuling/test");
        Class<?> clazz = myClassLoader.loadClass("java.lang.String");

        Object o = clazz.newInstance();
        Method method = clazz.getMethod("main", new Class[]{String[].class});
        method.invoke(o,null);
        System.out.println(clazz.getClassLoader());
    }
}
/**
 * Exception in thread "main" java.lang.SecurityException: Prohibited package name: java.lang
 *
 */