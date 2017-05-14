package future1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class Future<T> {
    private List<FutureListener> listeners = new ArrayList<>();
    private T data;//保存的数据，当数据被更新时就推送给listeners;


    public void addListener(FutureListener listener) {
        listeners.add(listener);
    }

    public void setData(T data) {
        this.data = data;
        notifyListeners();
    }

    public T get() {
        return data;
    }

    private void notifyListeners() {
        for (FutureListener listener : listeners) {
            CompleteEvent event = new CompleteEvent(this);
            listener.operationComplete(event);
        }
    }
}
