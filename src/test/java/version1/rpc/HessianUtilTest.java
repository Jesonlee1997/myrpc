package version1.rpc;

import org.junit.Test;
import version1.rpc.server.CalculatorImpl;
import version1.rpc.server.ServiceManager;

import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class HessianUtilTest {
    ServiceManager serviceManager = ServiceManager.getInstance();

    @Test
    public void readAndWriteObject() throws IOException {
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setArgs(1, 2);
        serviceRequest.setMethodName("add");
        serviceRequest.setServiceName("version1.rpc.Calculator");
        byte[] bytes = HessianUtil.requestToBytes(serviceRequest);

        ServiceRequest serviceRequest1 = HessianUtil.bytesToRequest(bytes);
        System.out.println(serviceRequest1);
        serviceManager.registerService(Calculator.class, CalculatorImpl.class);
        ServiceResponse response = serviceManager.invokeService(serviceRequest);
        System.out.println(response);
    }
}