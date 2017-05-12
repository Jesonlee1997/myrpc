package version1.rpc.server;

import version1.rpc.ServiceRequest;
import version1.rpc.ServiceResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务实现端的服务管理器
 * Created by JesonLee
 * on 2017/5/11.
 */
public class ServiceManager {
    private ServiceManager() {}
    private static ServiceManager serviceManager = new ServiceManager();
    public static ServiceManager getInstance() {
        return serviceManager;
    }

    //将Service的全限定名作为服务名
    private final Map<String, rpc.server.Service> serviceMap = new HashMap<>();

    //添加服务定义
    public void registerService(Class<?> interfaceClass, Object impl) {
        String serviceName = interfaceClass.getName();
        if (!serviceMap.containsKey(serviceName)) {
            rpc.server.Service service = new rpc.server.Service(interfaceClass, impl);
            serviceMap.put(serviceName, service);
        }
    }

    public void registerService(String serviceName, String implName) {
        if (!serviceMap.containsKey(serviceName)) {
            rpc.server.Service service = new rpc.server.Service(serviceName, implName);
            serviceMap.put(serviceName, service);
        }
    }

    public void registerService(Class<?> interfaceClass, Class<?> implClassName) {
        String serviceName = interfaceClass.getName();
        if (!serviceMap.containsKey(serviceName)) {
            rpc.server.Service service = new rpc.server.Service(implClassName, implClassName);
            serviceMap.put(serviceName, service);
        }
    }

    public Object invokeService(String serviceName, String methodName, Object...objects) {
        rpc.server.Service service = serviceMap.get(serviceName);
        Object o = service.invoke(methodName, objects);
        return o;
    }

    public ServiceResponse invokeService(ServiceRequest request) {
        String serviceName = request.getServiceName();
        String methodName = request.getMethodName();
        Object[] args = request.getArgs();
        Object result = invokeService(serviceName, methodName, args);
        return ServiceResponse.OK(result);
    }
}
