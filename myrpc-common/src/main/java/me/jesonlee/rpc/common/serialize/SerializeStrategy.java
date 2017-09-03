package me.jesonlee.rpc.common.serialize;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/9/3.
 */
public interface SerializeStrategy {
    byte[] objectToBytes(Object o) throws IOException;
    Object bytesToObject(byte[] bytes) throws IOException;
}
