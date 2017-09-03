package me.jesonlee.rpc.common.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class HessianStrategy implements SerializeStrategy {

    @Override
    public byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(outputStream);
        output.writeObject(o);
        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        return bytes;
    }

    @Override
    public Object bytesToObject(byte[] bytes) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(stream);
        Object o = input.readObject();
        input.close();
        return o;
    }
}
