package me.jesonlee.rpc.common.serialize;

import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/9/3.
 */
public class Serialization {
    private SerializeStrategy strategy;
    private static final Serialization DEFAULT_SERIALIZATIONY = new Serialization(new HessianStrategy());


    private Serialization(SerializeStrategy strategy) {
        this.strategy = strategy;
    }

    public static Serialization getSerialization(int strategy) {
        switch (strategy) {
            case SerializeStrategy.HESSIAN_STRATEGY:
                return DEFAULT_SERIALIZATIONY;
            case SerializeStrategy.KryoStrategy:
                return new Serialization(new KryoStrategy());
            default:
                return DEFAULT_SERIALIZATIONY;
        }
    }

    public static Serialization getDefaultSerialization() {
         return DEFAULT_SERIALIZATIONY;
    }

    public ServiceRequest getRequest(byte[] bytes) {
        ServiceRequest request;
        try {
            request = (ServiceRequest) strategy.bytesToObject(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestConvertException("Request格式错误");
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new RequestConvertException("Request类型错误");
        }
        return request;
    }

    public ServiceResponse getResponse(byte[] bytes) {
        ServiceResponse response;
        try {
            response = (ServiceResponse) strategy.bytesToObject(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseConvertException("Response格式错误");
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ResponseConvertException("Response类型错误");
        }
        return response;
    }

    public byte[] getBytes(ServiceRequest request) {
        byte[] bytes = null;
        try {
            bytes = strategy.objectToBytes(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public byte[] getBytes(ServiceResponse response) {
        byte[] bytes = null;
        try {
            bytes = strategy.objectToBytes(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
