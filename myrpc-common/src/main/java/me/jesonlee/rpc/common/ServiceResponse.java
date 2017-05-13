package me.jesonlee.rpc.common;

import java.io.Serializable;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class ServiceResponse implements Serializable {
    public static ServiceResponse OK(Object o) {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(200);
        response.setResult(o);
        return response;
    }

    public static ServiceResponse NotFound() {
        ServiceResponse response = new ServiceResponse();
        response.setStatus(404);
        return response;
    }

    private int status;//表示请求的状态 200请求成功
    private Object result;//表示服务调用的结果

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

    @Override
    public String toString() {
        return "me.jesonlee.rpc.common.ServiceResponse{" +
                "status=" + status +
                ", result=" + result +
                '}';
    }
}
