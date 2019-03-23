package net.nextabc.demo;

import net.nextabc.autowired.Autowired;

import java.io.File;

/**
 * @author 陈哈哈 (yoojiachen@gmail.com)
 * @version 1.0.0
 */
public class Test0 {

    private final Autowired file = Autowired.identify("BUILD.GRADLE");

    public void test() {
        File f = file.get();
        System.out.println(f.getAbsolutePath());
    }

}
