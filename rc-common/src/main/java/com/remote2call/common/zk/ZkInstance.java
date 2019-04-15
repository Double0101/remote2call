package com.remote2call.common.zk;

import com.remote2call.common.constant.Constant;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZkInstance {
    private static final Logger logger = LoggerFactory.getLogger(ZkInstance.class);

    private static volatile ZooKeeper zk;

    private String registerAddress;

    private CountDownLatch latch = new CountDownLatch(1);

    private static volatile ZkInstance zkInstance;

    private ZkInstance(String address) throws Exception {
        zk = null;
        this.registerAddress = address;
        try {
            zk = new ZooKeeper(registerAddress,
                    Constant.ZK_SESSION_TIMEOUT,
                    e -> {
                        if (e.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            latch.countDown();
                        }
                    });
            latch.await();
        } catch (IOException e) {
            logger.error("", e);
            zk = null;
        } catch (InterruptedException ie) {
            logger.error("", ie);
            zk = null;
        }
    }

    public static ZkInstance createZkCluster(String address) throws Exception {
        if (zkInstance == null) {
            synchronized (ZkInstance.class) {
                if (zkInstance == null) {
                    zkInstance = new ZkInstance(address);
                }
            }
        }
        return zkInstance;
    }

    public void addRootNode() throws Exception {
        try {
            Stat s = zk.exists(Constant.ZK_REGISTRY_PATH, false);
            if (s == null) {
                zk.create(Constant.ZK_REGISTRY_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            // TODO
        }
    }

    public void createNode(String data) {
        try {
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            // TODO
        } catch (InterruptedException e) {
            // TODO
        }
    }
}
