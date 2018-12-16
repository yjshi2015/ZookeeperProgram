package com.zookeeper.curatorapi;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/12/16.
 * @desc JDK自带的barrier来控制多线程的同步
 */
public class JDKBarrier {

    static CyclicBarrier barrier = new CyclicBarrier(3);

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(new Thread(new Runner("1号选手")));
        pool.submit(new Thread(new Runner("2号选手")));
        pool.submit(new Thread(new Runner("3号选手")));
        pool.shutdown();
    }
}
class Runner implements Runnable {
    private String name;
    public Runner(String name) {
        this.name = name;
    }

    public void run() {
        System.out.println(name + " 准备好了.");
        try {
            JDKBarrier.barrier.await();
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(name + "起跑！");
    }
}

