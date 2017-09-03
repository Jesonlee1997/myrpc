package me.jesonlee.rpc.client;


import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceProxy {
    private static Logger logger = LoggerFactory.getLogger(ServiceProxy.class);

    private RpcClient rpcClient = new RpcClient();

    //表示请求的id
    private AtomicLong requestId = new AtomicLong(0);

    private static volatile boolean sync = true;

    /**
     * 创建特定接口的代理
     *
     * @param clazz 必须是接口
     * @param <T>
     * @return 代理类实例
     */
    public <T> T createProxy(Class<T> clazz) {

        Object o = Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{clazz},
                new ServiceProxyHandler(clazz));
        return (T) o;
    }

    public void close() {
        rpcClient.close();
    }

    /**
     * invocationHandler
     */
    class ServiceProxyHandler implements InvocationHandler {

        private Class interfaceClass;//保留代理类实现的接口

        public ServiceProxyHandler(Class interfaceClass) {
            this.interfaceClass = interfaceClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            String serviceName = interfaceClass.getName();
            ServiceRequest request = new ServiceRequest();
            request.setId(requestId.getAndIncrement());
            request.setServiceName(serviceName);
            request.setMethodName(methodName);
            request.setArgs(args);
            ServiceResponse response;
            if (sync) {
                response = rpcClient.send(request);
            } else {
                response = rpcClient.sendAsync(request);
                if (response == null) {
                    return null;
                }
            }

            int status = response.getStatus();
            switch (status) {
                case ServiceResponse.OK:
                    logger.debug("request " + request.getId() +" invoke success");
                    return response.getResult();
                case ServiceResponse.SERVICE_NOT_FOUND:
                    logger.warn("service not found");
                    return null;
                case ServiceResponse.SERVICE_OFFLINE:
                    logger.warn("service offline");
                    return null;
                case ServiceResponse.TIMEOUT:
                    logger.warn("response timeout");
                    return null;
                case ServiceResponse.THREAD_EXCEPTION:
                    logger.warn("thread was interrupt");
                    return null;
                default:
                    return null;
            }
        }
    }

    public static void setSync() {
        sync = true;
    }

    public static void setAsync() {
        sync = false;
    }
}
