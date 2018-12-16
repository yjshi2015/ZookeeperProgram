package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 使用curator实现分布式barrier,线程同时启动，同时结束
 */
public class CuratorBarrier2 {

    static String path = "/curator_recipes_barrier_path";

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
                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(
                                client, path, 5);
                        Thread.sleep(Math.round(Math.random() * 3000));
                        System.out.println(Thread.currentThread().getName() + "号进入barrier");
                        //同进
                        barrier.enter();
                        System.out.println(Thread.currentThread().getName() + "号启动");
                        Thread.sleep(Math.round(Math.random() * 3000));

                        //同退
                        barrier.leave();
                        System.out.println(Thread.currentThread().getName() + "号退出");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}

