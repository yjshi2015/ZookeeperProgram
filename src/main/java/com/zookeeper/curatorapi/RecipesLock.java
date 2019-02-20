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
    static String path = "/zk/syj/ok";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(300)
            .retryPolicy(new ExponentialBackoffRetry(100, 2))
            .build();

    public static void main(String[] args) throws Exception{
        client.start();
        System.out.println("data:" + new String(client.getData().forPath(path)));
        final InterProcessMutex lock = new InterProcessMutex(client, path);
        for (int i=0; i<10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    long begin=0;
                    try {
                        latch.await();
                        begin = System.currentTimeMillis();
                        lock.acquire();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        //模拟业务处理
                        Thread.sleep(1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                        String orderNo = sdf.format(new Date());
                        long end = System.currentTimeMillis();
                        System.out.println("耗时：" + (end-begin)/1000 + " s，orderNO:" + orderNo);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
