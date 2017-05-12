package version1.rpc.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class Service {
    private Class<?> interfaceClass;
    private Object impl;
    private Map<String, Method> methodsMap = new HashMap<>();

    /**
     * 构造方法，解析interfaceClass下的方法
     * @param interfaceClass 方法的接口
     */
    public Service(Class<?> interfaceClass, Class<?> implClass) {
        this.interfaceClass = interfaceClass;
        try {
            impl = implClass.newInstance();
        } catch (Exception e) {
            System.out.println("类没有默认的构造方法");
        }
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            methodsMap.put(methodName, method);//TODO:方法名不能重名
        }
    }

    public Service(Class<?> interfaceClass, Object impl) {
        this.interfaceClass = interfaceClass;
        this.impl = impl;
        Method[] methods = interfaceClass.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            methodsMap.put(methodName, method);
        }
    }

    public Service(String interfaceName, String implName) {
        try {
            Class interfaceClass;
            Class implClass;
            interfaceClass = Class.forName(interfaceName);
            this.interfaceClass = interfaceClass;
            implClass = Class.forName(implName);
            impl = implClass.newInstance();
            Method[] methods = interfaceClass.getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                methodsMap.put(methodName, method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object invoke(String methodName, Object...objects) {
        Method method = methodsMap.get(methodName);
        Object result = null;
        try {
            result = method.invoke(impl, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
