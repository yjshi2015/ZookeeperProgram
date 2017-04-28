package com.zookeeper.javaapi;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
/**
 * ͨ��sessionid��passwd�ָ��Ự
 * */
public class Zookeeper_Constructor_Usage_With_SID_PASSWD implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception{
		//�����ػ�
		ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 
				5000,
				new Zookeeper_Constructor_Usage_With_SID_PASSWD());
		connectedSemaphore.await();
		long sessionId = zooKeeper.getSessionId();
		byte[] passwd = zooKeeper.getSessionPasswd();
		
		//ʹ�ô���sessionid��passwd���ָ��Ự
		zooKeeper = new ZooKeeper("127.0.0.1:2181",
				5000,
				new Zookeeper_Constructor_Usage_With_SID_PASSWD(),
				1L,
				"test".getBytes());
		
		//ʹ����ȷ��sessionid��passwd���ָ��Ự 
		zooKeeper = new ZooKeeper("127.0.0.1:2181",
				5000,
				new Zookeeper_Constructor_Usage_With_SID_PASSWD(),
				sessionId,
				passwd);
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	public void process(WatchedEvent event) {
		System.out.println("receive watched event : " + event);
		if(KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

}
