package com.zookeeper.javaapi;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

/**
 * 创建节点，使用同步(sync)接口
 * */
public class Zookeeper_Create_API_Sync_Node implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",
				5000,
				new Zookeeper_Create_API_Sync_Node());
		connectedSemaphore.await();
		String path1 = zooKeeper.create("/zk-test-ephemeral-",
				"".getBytes(),
				Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		System.out.println("Success create znode1 : " + path1);
		
		String path2 = zooKeeper.create("/zk-test-ephemeral-",
				"".getBytes(),
				Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("Success create znode2 : " + path2);
	}

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}
}
