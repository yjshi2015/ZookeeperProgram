package com.zookeeper.curatorapi;

/**
 * Created by Administrator on 2018/12/15.
 */
public class MainTest {

    public static void main(String[] args) {
        System.out.println("1");
        System.out.println(Integer.MAX_VALUE/1000/60);

        String str = "BEA";
        changde(str);
        System.out.println("str:"+str);
    }

    private static void changde(String str) {
        str = str.replace("A", "E");
        str = str.toLowerCase();
    }
}
