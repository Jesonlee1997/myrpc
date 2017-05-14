package me.jesonlee.rpc.common;

import java.io.Serializable;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceResponse implements Serializable {
    private static final int OK = 200;
    private static final int SERVICE_NOT_FOUND = 404;
    private static final int PROVIDER_NOT_FOUND = 406;

    public static ServiceResponse OK(Object o, long id) {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(OK);
        response.setResult(o);
        return response;
    }

    public static ServiceResponse ServiceNotFound(long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setStatus(SERVICE_NOT_FOUND);
        return response;
    }

    public static ServiceResponse ProviderNotFound(long id) {
        ServiceResponse response = new ServiceResponse();
        response.setId(id);
        response.setStatus(PROVIDER_NOT_FOUND);
        return response;
    }

    private int status;//表示请求的状态 200请求成功
    private Object result;//表示服务调用的结果
    private long id;

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
