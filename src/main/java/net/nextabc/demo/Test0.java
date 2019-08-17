package net.nextabc.demo;

import net.nextabc.autowired.Autowired;

import java.io.File;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class Test0 {

    private final Autowired<File> file = Autowired.id("BUILD.GRADLE");

    public void test() {
        File f = file.getBean();
        System.out.println(f.getAbsolutePath());
    }

}
