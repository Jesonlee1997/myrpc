package zk;

import me.jesonlee.rpc.common.ServiceRegistry;
import org.junit.Test;

import java.util.List;

/**
 * Created by JesonLee
 * on 2017/5/13.
 */
public class ServiceRegistryTest {
    private String zooHosts = "192.168.56.101:2181,192.168.56.102:2181,192.168.56.103:2181";
    private ServiceRegistry registry;

    @Test
    public void registerService() throws Exception {
        registry.registerService("version1.rpc.me.jesonlee.rpc.client.Calculator", "127.0.0.1:8080");
        registry.registerService("version1.rpc.me.jesonlee.rpc.client.Calculator", "127.0.0.1:8081");
        registry.registerService("version1.rpc.me.jesonlee.rpc.client.Calculator", "127.0.0.1:8082");
        Thread.sleep(20000);
    }

    @Test
    public void getProviderAddress() throws Exception {
        List<String> providerAddress = registry.getProviderAddress("version1.rpc.me.jesonlee.rpc.client.Calculator", null);
        for (String address : providerAddress) {
            System.out.println(address);
        }
    }

}