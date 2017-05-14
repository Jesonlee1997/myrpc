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

    private volatile boolean sync = true;

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
            if (sync) {
                long start = System.currentTimeMillis();
                ServiceResponse response = rpcClient.send(request);
                long end = System.currentTimeMillis();
                System.out.println("同步调用的时间："+(end - start));//TODO:remove
                int status = response.getStatus();
                switch (status) {
                    case 200:
                        logger.debug("request " + request.getId() +" invoke success");
                        return response.getResult();
                    case 404:
                        logger.info("provider not found");
                        return null;
                    default:
                        //TODO:记录日志
                        return null;
                }
            }
            rpcClient.sendAsync(request);
            return null;
        }
    }

    public void setSync() {
        sync = true;
    }

    public void setAsync() {
        sync = false;
    }
}
