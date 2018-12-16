package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 使用curator实现分布式barrier
 */
public class CuratorBarrier {

    static String path = "/curator_recipes_barrier_path";
    static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception {
        for (int i=0; i<5; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        CuratorFramework client = CuratorFrameworkFactory.builder()
                                .connectString("127.0.0.1:2181")
                                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                                .sessionTimeoutMs(3000)
                                .build();
                        client.start();
                        barrier = new DistributedBarrier(client, path);
                        System.out.println(Thread.currentThread().getName() + "号barrier设置");
                        barrier.setBarrier();
                        barrier.waitOnBarrier();
                        System.out.println(Thread.currentThread().getName() + "号barrier启动");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        Thread.sleep(2000);
        barrier.removeBarrier();
    }
}

