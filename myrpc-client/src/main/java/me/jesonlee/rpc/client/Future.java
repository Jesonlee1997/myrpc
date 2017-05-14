package me.jesonlee.rpc.client;

/**
 * 保存调用的结果，线程可以通过get()来获得所需要的数据对象
 * Created by JesonLee
 * on 2017/5/14.
 */
public class Future {
    private Object data;
    private volatile boolean complete;

    public Object get() {
        if (!complete) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public void setData(Object data) {
        synchronized (this) {
            complete = true;
            this.data = data;
            notify();
        }
    }
}
