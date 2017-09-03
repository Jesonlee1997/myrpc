package me.jesonlee.rpc.common.serialize;

import me.jesonlee.rpc.common.ServiceRequest;
import me.jesonlee.rpc.common.ServiceResponse;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/9/3.
 */
public class SerializeUtil {
    private static SerializeStrategy strategy;

    static {
        strategy = new HessianStrategy();
    }


    public static ServiceRequest getRequest(byte[] bytes) {
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

    public static ServiceResponse getResponse(byte[] bytes) {
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

    public static byte[] getBytes(ServiceRequest request) {
        byte[] bytes = null;
        try {
            bytes = strategy.objectToBytes(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] getBytes(ServiceResponse response) {
        byte[] bytes = null;
        try {
            bytes = strategy.objectToBytes(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
