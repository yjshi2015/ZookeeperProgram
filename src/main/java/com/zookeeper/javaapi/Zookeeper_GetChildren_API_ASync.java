package com.zookeeper.javaapi;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/**
 * zookeeper API获取子节点列表，使用异步(ASync)接口
 * */
public class Zookeeper_GetChildren_API_ASync implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	
	public static void main(String[] args) throws Exception{
		String path = "/zk-book2";
		zk = new ZooKeeper("127.0.0.1:2181", 
				5000, 
				new Zookeeper_GetChildren_API_ASync());
		connectedSemaphore.await();
		
		//创建临时节点
		zk.create(path, "".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		zk.create(path + "/c1", "".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		//通过回调方式获取节点列表,同时通过TRUE参数注册watch
		zk.getChildren(path, true, new IChildren2Callback(), null);
		
		zk.create(path + "/c2", "".getBytes(), 
				Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		
		Thread.sleep(Integer.MAX_VALUE);
	}

	public void process(WatchedEvent event) {
		if (event.getState() == KeeperState.SyncConnected) {
			if (event.getType() == EventType.None && event.getPath() == null) {
				connectedSemaphore.countDown();
			} else if (event.getType() == EventType.NodeChildrenChanged) {
				try {
					System.out.println("reget children : " 
							+ zk.getChildren(event.getPath(), true));
				} catch (Exception e) {}
			}
		}
	}
}

class IChildren2Callback implements AsyncCallback.Children2Callback {

	public void processResult(int rc, String path, Object ctx,
			List<String> children, Stat stat) {
		System.out.println("get children node result : [response code : "
				+ rc + ",param path : " + path + ", ctx : " + ctx
				+ ",children list : " + children + ", stat : "
				+ stat + "]");
	}
	
}