package com.zookeeper.javaapi;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * zook API获取子节点列表，使用同步(sync)接口
 * */
public class Zookeeper_GetChildren_API_Sync implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception{
		String path = "/zk-book1";
		zk = new ZooKeeper("127.0.0.1:2181",
				5000,
				new Zookeeper_GetChildren_API_Sync());
		connectedSemaphore.await();
		
		//只有持久节点才能创建子节点
		zk.create(path, "".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create(path + "/c1", "".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		//通过TRUE参数注册watch事件
		List<String> childrenList = zk.getChildren(path, true);
		System.out.println(childrenList);
		
		zk.create(path + "/c2", "c2".getBytes(),
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		if (KeeperState.SyncConnected == event.getState()) {
			if (EventType.None == event.getType() && event.getPath() == null) {
				connectedSemaphore.countDown();
			} else if (EventType.NodeChildrenChanged == event.getType()) {
				try {
					System.out.println("reget children : " + 
				zk.getChildren(event.getPath(), true));
				} catch (Exception e) {}
			}
		}
	}
}
