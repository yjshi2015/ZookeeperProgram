package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 分布式计数器
 */
public class RecipesDistAtomicInt {

    static String path = "/curator_recipes_distatomicint_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        DistributedAtomicInteger atomicInteger =
                new DistributedAtomicInteger(client, path,
                        new RetryNTimes(3, 1000));
        AtomicValue<Integer> rc = atomicInteger.add(8);
        System.out.println("result:" + rc.succeeded());
    }
}
