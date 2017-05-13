import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class TestZookeeper {
    private ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException, InterruptedException {
        //第一个参数是Zookeeper服务器列表
        String CONN = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
        //第二个参数是Zookeeper，通讯的过期时长
        Integer sessionTimeOut = 5000;
        //第三个是watcher，监听连接上zookeeper后的事件
        //异步连接  TODO:CountDownLatch
        CountDownLatch latch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(CONN, sessionTimeOut, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                System.out.println("建立连接");
                latch.countDown();
            }
        });
        latch.await();
    }

    @Test
    public void testCreate() throws KeeperException, InterruptedException {
        //zooKeeper.delete("/app1/context1", -1);
        String s = zooKeeper.create("/app1/context1",
                "127.0.0.1:8080".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println(s);
    }

    @Test
    public void testGetChildren() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/app1/context1", false);
        System.out.println(children);
        for (String child : children) {
            System.out.println(child);
        }
    }
}
