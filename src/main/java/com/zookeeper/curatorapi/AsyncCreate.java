package com.zookeeper.curatorapi;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 异步创建
 */
public class AsyncCreate {

    static String path = "/zk-book";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static CountDownLatch semaphore = new CountDownLatch(2);
    static ExecutorService pool = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
        client.start();
        System.out.println("current thread name :" + Thread.currentThread().getName());

        //传入自定义的线程池
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println("event : " + JSONObject.toJSONString(curatorEvent));
                        System.out.println("diy--->thread of process Result:" + Thread.currentThread().getName());
                        semaphore.countDown();
                    }
                }, pool).forPath(path, "init".getBytes());

        //不传入自定义的线程池，由默认的EventThread线程来处理回调逻辑
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                        System.out.println("event : " + JSONObject.toJSONString(curatorEvent));
                        System.out.println("default--->Thread of processResult :" + Thread.currentThread().getName());
                        semaphore.countDown();
                    }
                }).forPath("/zk-syj", "syj".getBytes());

        semaphore.await();
        pool.shutdown();
    }
}
