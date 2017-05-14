package future1;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class CompleteEvent {
    private Future future;

    public CompleteEvent(Future future) {
        this.future = future;
    }

    public Future getSource() {
        return future;
    }
}
