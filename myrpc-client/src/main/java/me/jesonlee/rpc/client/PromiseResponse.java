package me.jesonlee.rpc.client;

import io.netty.channel.ChannelPromise;
import me.jesonlee.rpc.common.ServiceResponse;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class PromiseResponse {
    private ChannelPromise promise;
    private ServiceResponse response;

    //用于异步完成操作后将serviceMap
    private Listener listener;

    public PromiseResponse(ChannelPromise promise) {
        this.promise = promise;
    }

    /**
     * 同步获得调用结果
     * @return
     * @throws InterruptedException
     */
    public ServiceResponse getResponse() throws InterruptedException {
        promise.await(3000);//TODO:将服务调用超时设为属性
        return response;
    }

    public void setResponse(ServiceResponse response) {
        this.response = response;
        promise.setSuccess();
    }

    public void addListener(Listener listener) {
        this.listener = listener;
    }

    public Object get(int timeout) {
        if (promise.isSuccess()) {
            return response.getResult();
        } else {
            try {
                boolean success = promise.await(timeout);
                if (success) {
                    listener.update();
                    return response.getResult();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    interface Listener {
        void update();
    }
}
