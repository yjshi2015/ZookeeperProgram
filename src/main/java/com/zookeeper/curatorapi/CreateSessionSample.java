package com.zookeeper.curatorapi;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * Created by Administrator on 2018/12/15.
 */
public class CreateSessionSample {

    public static void main(String[] args) throws Exception {
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(
                3000,
                2);
        //创建连接
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "127.0.0.1:2181",
                2000,
                3000,
                retryPolicy);
        client.start();
        //创建节点
//        client.create().forPath("/myzk/sunday","happy".getBytes());
//        System.out.println("create node ok");
//        Thread.sleep(5000);

        client.delete().deletingChildrenIfNeeded().forPath("/myzk");
        System.out.println("删除/myzk节点OK");

        String ephemeralPath = "/myzk_e/sunday_e";
        //创建临时节点
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL).forPath(ephemeralPath);
        System.out.println("创建临时节点OK");

        //获取版本
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/myzk_e/sunday_e");
        System.out.println("/myzk_e/sunday_e 's old version:"+ JSONObject.toJSONString(stat));

        //更新节点数据
        int newVersion = client.setData().withVersion(stat.getVersion()).forPath("/myzk_e/sunday_e").getVersion();
        System.out.println(ephemeralPath + " 的新版本号为：" + newVersion);

        //再次更新节点
        try {
            client.setData().withVersion(stat.getVersion()).forPath(ephemeralPath, "init".getBytes());
        } catch (Exception e) {
            System.out.println("使用旧版本号更新节点数据失败：" + e.getMessage());
        }
    }
}
