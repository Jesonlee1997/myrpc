package me.jesonlee.rpc.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO:使用Spring自动注入
 * Created by JesonLee
 * on 2017/5/14.
 */
public class RpcContext {
    private static RpcContext context = new RpcContext();
    public static RpcContext getInstance() {
        return context;
    }

    private Map<Thread, PromiseResponse> threadFutureMap = new ConcurrentHashMap<>();

    public void addPromise(PromiseResponse response) {
        threadFutureMap.put(Thread.currentThread(), response);
    }

    public PromiseResponse getPromise() {
        return threadFutureMap.get(Thread.currentThread());
    }
}
