package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 分布式锁
 */
public class RecipesLock {

    static CountDownLatch latch = new CountDownLatch(1);
    static String path = "/curator_recipes_lock_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) {
        client.start();
        final InterProcessMutex lock = new InterProcessMutex(client, path);
        for (int i=0; i<10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        lock.acquire();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println(orderNo);
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        latch.countDown();
    }
}
