package com.zookeeper.curatorapi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/12/16.
 * @desc 并发case
 */
public class RecipesNoLock {

    static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        for (int i=0; i<10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println(orderNo);
                }
            }).start();
        }

        latch.countDown();
    }
}
