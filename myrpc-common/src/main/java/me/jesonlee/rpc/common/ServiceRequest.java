package me.jesonlee.rpc.common;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceRequest implements Serializable {
    private String serviceName;
    private String methodName;
    private Object[] args;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object...args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "me.jesonlee.rpc.common.ServiceRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
