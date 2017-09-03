package me.jesonlee.rpc.common;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by JesonLee
 * on 2017/5/13.
 */
public class ServiceRegistry {
    private final String servicesRootPath = "/services";
    private ZooKeeper zooKeeper;


    public ServiceRegistry(String hosts) {
        try {
            Integer sessionTimeOut = 5000;//通讯的过期时长
            CountDownLatch latch = new CountDownLatch(1);
            zooKeeper = new ZooKeeper(hosts, sessionTimeOut, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    System.out.println("与ZooKeeper服务器建立连接");
                    latch.countDown();
                }
            });

            latch.await();
            if (zooKeeper.exists(servicesRootPath, false) == null) {
                zooKeeper.create(servicesRootPath,
                        "".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }


    /**
     * 注册服务提供者
     * 在服务节点下建立子节点存储服务提供者的IP地址
     *
     * @param serviceName     服务的名称
     * @param providerAddress 服务提供者的地址，包括IP地址和端口号
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void registerService(String serviceName, String providerAddress) throws KeeperException, InterruptedException {
        String servicePath = servicesRootPath + "/" + serviceName;
        if (zooKeeper.exists(servicePath, false) == null) {
            zooKeeper.create(servicePath,
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
        }
        providerAddress = servicePath+ "/" + providerAddress;//具体的子节点
        if (zooKeeper.exists(providerAddress, false) == null) {
            zooKeeper.create(providerAddress,
                    "".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL);
        }
    }

    public List<String> getProviderAddress(String serviceName, Watcher watcher) throws KeeperException, InterruptedException {
        String servicePath = servicesRootPath + "/" + serviceName;
        if (zooKeeper.exists(servicePath, false) != null) {
            return zooKeeper.getChildren(servicePath, watcher);
        }
        return null;
    }

    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
