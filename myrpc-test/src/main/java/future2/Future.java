package future2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class Future {
    private Object data;
    private final Lock lock = new ReentrantLock();

    private final Condition complete = lock.newCondition();

    public Object get() {
        lock.lock();
        try {
            complete.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return data;
    }

    public void setData(Object data) {
        lock.lock();
        this.data = data;
        complete.signal();
        lock.unlock();
    }
}
