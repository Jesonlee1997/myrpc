package me.jesonlee.rpc.server;

import me.jesonlee.rpc.common.ServiceRegistry;
import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;
import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务实现端的服务管理器，对请求进行路由调用实际服务
 * Created by JesonLee
 * on 2017/5/11.
 */
public class ServiceManager {
    private ServiceManager() {}
    private static ServiceManager serviceManager = new ServiceManager();
    public static ServiceManager getInstance() {
        return serviceManager;
    }

    //TODO:使用spring注入
    private ServiceRegistry registry = new ServiceRegistry("192.168.56.101");


    //将Service的全限定名作为服务名
    private final Map<String, Service> serviceMap = new HashMap<>();

    //添加服务定义
    public void addService(Service service) {
        String serviceName = service.getServiceName();
        if (!serviceMap.containsKey(serviceName)) {
            serviceMap.put(serviceName, service);
        }
    }

    public void addService(Class<?> interfaceClass, Class<?> implClassName) {
        Service service = new Service(implClassName, implClassName);
        String serviceName = interfaceClass.getName();
        if (!serviceMap.containsKey(serviceName)) {
            serviceMap.put(serviceName, service);
        }
    }

    public ServiceResponse invokeService(ServiceRequest request) {
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        Service service = serviceMap.get(serviceName);
        if (service == null) {
            return ServiceResponse.NotFound();
        }
        Object result = service.invoke(methodName, args);
        return ServiceResponse.OK(result);
    }

    public void registerAllServices(int port) {
        String hostAddress = getLocalAddress();
        for (String serviceName : serviceMap.keySet()) {
            try {
                registry.registerService(serviceName, hostAddress +":" +port);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerService(Service service, int port) {
        String hostAddress = getLocalAddress();
        try {
            registry.registerService(service.getServiceName(), hostAddress +":" +port);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private String getLocalAddress() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address.getHostAddress();
    }
}
