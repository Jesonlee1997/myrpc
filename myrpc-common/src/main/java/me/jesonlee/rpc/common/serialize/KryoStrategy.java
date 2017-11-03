package me.jesonlee.rpc.common.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/9/28.
 */
public class KryoStrategy implements SerializeStrategy {
    private Kryo kryo = new Kryo();


    @Override
    public byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        kryo.writeObject(new Output(bytes), o);
        return bytes.toByteArray();
    }

    @Override
    public Object bytesToObject(byte[] bytes) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        Object o = kryo.readObject(new Input(input), Object.class);
        return o;
    }
}
