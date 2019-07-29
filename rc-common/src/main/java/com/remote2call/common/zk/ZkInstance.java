package com.remote2call.common.zk;

import com.remote2call.common.constant.Constant;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class ZkInstance {
    private static final Logger logger = LoggerFactory.getLogger(ZkInstance.class);

    private static volatile ZooKeeper zk;

    private String registerAddress;

    private static volatile ZkInstance zkInstance;

    private ZkInstance(String address) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
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
        } catch (Exception e) {
            logger.error("[remote2call] zookeeper connect fail", e);
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

    public boolean addMasterNode(String path) {
        byte[] bytes = UUID.randomUUID().toString().getBytes();
        try {
            Stat s = zk.exists(path, false);
            if (s == null) {
                zk.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                return true;
            } else {
                return false;
            }
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NodeExistsException) return false;
        } catch (InterruptedException e) {
        }
        return checkOwner(path, bytes);
    }

    private boolean checkOwner(String path, byte[] bytes) {
        for (int i = 0; i < 3; ++i) {
            try {
                Stat stat = new Stat();
                byte[] bs = zk.getData(path, false, stat);
                if (Arrays.equals(bytes, bs)) return true;
            } catch (KeeperException e) {
                if (e instanceof KeeperException.NoNodeException) {
                    return false;
                }
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    public boolean createNode(String nodePath,
                              byte[] data,
                              CreateMode createMode)
            throws KeeperException, InterruptedException{
        Stat stat = null;
        if (CreateMode.PERSISTENT == createMode || CreateMode.EPHEMERAL == createMode) {
            stat = zk.exists(nodePath, false);
        }
        if (stat == null) {
            String path = zk.create(nodePath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
            logger.debug("[remote2call] create zookeeper node ({} => {})", path, new String(data));
            return true;
        }
        return false;
    }

    public List watchAll() {
        return watchNode(this.zk);
    }

    public List watchNode(final ZooKeeper zk) {
        List<String> dataList = new ArrayList<>();
        try {
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, event -> {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            });
            nodeList.forEach(node -> {
                try {
                    dataList.add(
                            new String(zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node,
                                    false,
                                    null))
                    );
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public void disconnect() throws InterruptedException {
        if (zk != null) {
            logger.info("zookeeper disconnect ...");
            zk.close();
            logger.info("zookeeper has disconnected");
        }
    }

    private void updateConnectServer() {

    }
}
