package com.zookeeper.javaapi;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 创建节点，使用异步(async)接口
 * */
public class Zookeeper_Create_API_ASync_Node implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

	public static void main(String[] args) throws Exception{

		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",
				5000,
				new Zookeeper_Create_API_ASync_Node());
		connectedSemaphore.await();
		
		//第一次创建/zk-test-ephemeral节点
		zooKeeper.create("/zk-test-ephemeral", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				new IStringCallback(), "i am context");

		//第二次创建/zk-test-ephemeral节点
		zooKeeper.create("/zk-test-ephemeral", "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,
				new IStringCallback(), "i am context");
		
		//创建同名但节点类型不同的节点
		zooKeeper.create("/zk-test-ephemeral", "".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,
				new IStringCallback(), "i am context");
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		System.out.println("receive watched event : " + event);
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

}

class IStringCallback implements AsyncCallback.StringCallback {
	public void processResult(int rc, String path, Object ctx, String nodeName) {
		System.out.println("create path result : [" + rc + ", " + path + ", "
				+ ctx + ", real path name : " + nodeName + "]");
	}

}
