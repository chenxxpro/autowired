package net.nextabc.demo;

import net.nextabc.autowired.Autowired;

import java.io.File;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class Test1 {

    private static final Autowired gradlew = Autowired.identify("GRADLEW");

    public void test() {
        File f = gradlew.get();
        System.out.println(f.getAbsolutePath());
    }

}
