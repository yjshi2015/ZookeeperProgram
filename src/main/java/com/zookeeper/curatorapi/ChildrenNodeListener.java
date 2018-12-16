package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by Administrator on 2018/12/16.
 * 监听子节点的创建、数据变更、删除
 */
public class ChildrenNodeListener {

    static String path = "/zk-book";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(100, 3))
            .sessionTimeoutMs(5000)
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        final PathChildrenCache cache = new PathChildrenCache(client, path, true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client,
                                   PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("增加子节点,data:" + event.getData() + ",path:" + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("子节点数据变更,data:" + event.getData() + ",path:" + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("删除子节点,data:" + event.getData() + ",path:" + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        });
        //创建节点
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        Thread.sleep(1000);
        //添加子节点
        client.create().withMode(CreateMode.PERSISTENT).
                forPath(path+"/c1");
        Thread.sleep(1000);
        //删除子节点
        client.delete().forPath(path+"/c1");
        Thread.sleep(1000);
        //删除父节点
//        client.delete().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
