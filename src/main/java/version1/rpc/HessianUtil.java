package version1.rpc;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class HessianUtil {

    public static ServiceRequest bytesToRequest(byte[] bytes) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(stream);
        Object request = input.readObject();
        if (request instanceof ServiceRequest) {
            return (ServiceRequest) request;
        } else {
            System.out.println("请求错误：传输的不是TransferObject对象");
            return null;
        }

    }

    public static byte[] requestToBytes(Object o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(outputStream);
        output.writeObject(o);
        return outputStream.toByteArray();
    }

    public static ServiceResponse bytesToResponse(byte[] bytes) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        HessianInput input = new HessianInput(stream);
        Object request = input.readObject();
        if (request instanceof ServiceResponse) {
            return (ServiceResponse) request;
        } else {
            System.out.println("请求错误：传输的不是ServiceResponse对象");
            return null;
        }

    }

    public static byte[] responseToBytes(Object o) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HessianOutput output = new HessianOutput(outputStream);
        output.writeObject(o);
        return outputStream.toByteArray();
    }
}
