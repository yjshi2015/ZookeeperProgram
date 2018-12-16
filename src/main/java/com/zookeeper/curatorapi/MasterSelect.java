package com.zookeeper.curatorapi;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Created by Administrator on 2018/12/16.
 * @desc master选举
 */
public class MasterSelect {

    static String masterPath = "/curator_recipes_master_path";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .sessionTimeoutMs(3000)
            .build();

    public static void main(String[] args) throws InterruptedException {
        client.start();
        LeaderSelector selector = new LeaderSelector(client,
                masterPath,
                new LeaderSelectorListenerAdapter() {
                    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                        System.out.println("成为master角色");
                        Thread.sleep(1000);//执行业务逻辑
                        System.out.println("完成master操作，释放master权利");
                    }
                });
        //不断去抢锁
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
