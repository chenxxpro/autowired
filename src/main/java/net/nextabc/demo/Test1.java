package net.nextabc.demo;

import net.nextabc.autowired.Autowired;

import java.io.File;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class Test1 {

    private static final Autowired<File> gradlew = Autowired.id("GRADLEW");

    public void test() {
        File f = gradlew.bean();
        System.out.println(f.getAbsolutePath());
    }

}
