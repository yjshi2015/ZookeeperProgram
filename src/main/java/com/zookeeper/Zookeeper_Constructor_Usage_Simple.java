package com.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
/**
 * 根据zookeeper原生的Java API创建一个基本的zookeeper回话实例
 * */
public class Zookeeper_Constructor_Usage_Simple implements Watcher{

	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception{
		ZooKeeper zooKeeper = 
				new ZooKeeper("127.0.0.1:2181", 
						5000, 
						new Zookeeper_Constructor_Usage_Simple());
		System.out.println(zooKeeper.getState());
		try {
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			System.out.println("zookeeper session established failed !");
		}
		System.out.println("zookeeper session established success !");

	}
	
	public void process(WatchedEvent event) {
		System.out.println("receive watched event : " + event);
		if (KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}

}
