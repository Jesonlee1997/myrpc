package me.jesonlee.rpc.common.serialize;

/**
 * Created by JesonLee
 * on 2017/9/3.
 */
public class ResponseConvertException extends RuntimeException {
    public ResponseConvertException() {
        super();
    }

    public ResponseConvertException(String message) {
        super(message);
    }
}
