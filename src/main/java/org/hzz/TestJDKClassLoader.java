package org.hzz;

public class TestJDKClassLoader {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println(com.sun.crypto.provider.DESKeyFactory.class.getClassLoader());
        System.out.println(TestJDKClassLoader.class.getClassLoader());
    }
}
/**
 * null
 * sun.misc.Launcher$ExtClassLoader@1b6d3586
 * sun.misc.Launcher$AppClassLoader@18b4aac2
 */