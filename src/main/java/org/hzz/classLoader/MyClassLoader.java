package org.hzz.classLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader{
    private String classpath;
    public MyClassLoader(String classpath){ this.classpath = classpath; }

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

    public static void main(String[] args) throws Exception {
        MyClassLoader myClassLoader = new MyClassLoader("D:/Code/tuling/test");
        Class<?> clazz = myClassLoader.loadClass("org.hzz.Info");
        Class<?> Strclazz = myClassLoader.loadClass("java.lang.String");

        Object o = clazz.newInstance();
        Method method = clazz.getMethod("callMe", null);
        method.invoke(o,null);
        System.out.println(clazz.getClassLoader());
    }
}
/**
 * =======自己的加载器加载类调用方法=======
 * org.hzz.classLoader.MyClassLoader@232204a1
 */