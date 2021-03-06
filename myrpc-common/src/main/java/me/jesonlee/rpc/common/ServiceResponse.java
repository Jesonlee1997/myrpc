package me.jesonlee.rpc.common;

import java.io.Serializable;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceResponse implements Serializable {
    public static final int OK = 200;
    public static final int SERVICE_NOT_FOUND = 404;
    public static final int PROVIDER_NOT_FOUND = 406;
    public static final int TIMEOUT = 407;
    public static final int THREAD_EXCEPTION = 408;
    public static final int BAD_TYPE = 409;
    public static final int SERVICE_OFFLINE = 410;

    public static ServiceResponse OK(Object o, long id) {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(OK);
        response.setResult(o);
        response.setId(id);
        return response;
    }

    public static ServiceResponse ServiceNotFound(String serviceName, long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setResult(serviceName);
        response.setStatus(SERVICE_NOT_FOUND);
        return response;
    }

    public static ServiceResponse ServiceOffline(String serviceName, long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setResult(serviceName);
        response.setStatus(SERVICE_OFFLINE);
        return response;
    }

    public static ServiceResponse ProviderNotFound(String serviceName, long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setResult(serviceName);
        response.setStatus(PROVIDER_NOT_FOUND);
        return response;

    }

    public static ServiceResponse Timeout(long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setStatus(TIMEOUT);
        return response;
    }

    public static ServiceResponse ThreadException() {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(THREAD_EXCEPTION);
        return response;
    }

    /**
     * 服务端收到的请求阿虎局不是ServiceRequest类型
     * 或者客户端收到数据不是ServiceResponse类型
     * @return
     */
    public static ServiceResponse BadType() {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(BAD_TYPE);
        return response;
    }

    private int status;//表示请求的状态 200请求成功
    private Object result;//表示服务调用的结果
    private long id = -1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
