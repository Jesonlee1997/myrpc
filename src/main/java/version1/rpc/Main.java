package version1.rpc;

import rpc.server.ServiceManager;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class Main {
    public static void main(String[] args) {
        ServiceManager serviceManager = new ServiceManager();
        serviceManager.registerService("rpc.Calculator", "rpc.server.CalculatorImpl");

        Object o = serviceManager.invokeService("rpc.Calculator", "add", 1, 2);
        System.out.println(o);
    }
}
