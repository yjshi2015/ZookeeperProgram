package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 监听节点的数据变更、创建(不包含子节点)
 */
public class NodeListener {

    static String path = "/zk-book/nodecache";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws Exception {
        client.start();
        //创建节点
        client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .forPath(path, "init".getBytes());
        //创建节点监听
        final NodeCache cache = new NodeCache(client, path, false);
        //设为true，则第一次启动时就会从zookeeper服务器读取节点内容放到cache中
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println("node updated,new data:" +
                new String(cache.getCurrentData().getData()));
            }
        });

        //节点赋值
        client.setData().forPath(path, "u".getBytes());
        Thread.sleep(1000);

        //节点删除,无法监听到
//        client.delete().deletingChildrenIfNeeded().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }
}
