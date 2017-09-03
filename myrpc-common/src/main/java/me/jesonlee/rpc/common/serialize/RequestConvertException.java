package me.jesonlee.rpc.common.serialize;

/**
 * Created by JesonLee
 * on 2017/9/3.
 */
public class RequestConvertException extends RuntimeException {
    public RequestConvertException() {
        super();
    }

    public RequestConvertException(String message) {
        super(message);
    }
}
