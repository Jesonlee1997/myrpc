package version1.rpc.client;

import version1.rpc.Calculator;
import version1.rpc.ServiceRequest;
import version1.rpc.ServiceResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceProxy {
    /**
     * 创建特定接口的代理
     * @param clazz 必须是接口
     * @param <T>
     * @return 代理类实例
     */
    public <T>T createProxy(Class<T> clazz) {

        Object o = Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{clazz},
                new ServiceProxyHandler(clazz));
        return (T) o;
    }

    public static void main(String[] args) {
        ServiceProxy proxy = new ServiceProxy();
        Calculator calculator = proxy.createProxy(Calculator.class);
        int add = calculator.add(1, 2);
        System.out.println(add);
    }
}

class ServiceProxyHandler implements InvocationHandler {
    private Client client = new Client("127.0.0.1", 8080);
    private Class interfaceClass;//保留代理类实现的接口

    public ServiceProxyHandler(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        String serviceName = interfaceClass.getName();
        ServiceRequest request = new ServiceRequest();
        request.setServiceName(serviceName);
        request.setMethodName(methodName);
        request.setArgs(args);
        ServiceResponse response = client.send(request);
        Object result = response.getResult();
        return result;
    }
}

