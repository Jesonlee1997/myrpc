import me.jesonlee.rpc.client.PromiseResponse;
import me.jesonlee.rpc.client.RpcContext;
import me.jesonlee.rpc.client.ServiceProxy;
import me.jesonlee.rpc.common.Calculator;
import org.junit.Test;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class TestMain {
    private RpcContext rpcContext = RpcContext.getInstance();
    private int num = 1;

    @Test
    //测试同步调用
    public void test1() {
        long start = System.currentTimeMillis();
        ServiceProxy proxy = new ServiceProxy();
        proxy.setSync();

        Calculator calculator = proxy.createProxy(Calculator.class);
        long end = System.currentTimeMillis();
        System.out.println(end - start);


        Integer[] results = new Integer[num];

        start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            results[i] = calculator.add(0, i*2);
        }

        end = System.currentTimeMillis();
        System.out.println(end - start);

        for (int result : results) {
            //System.out.println(result);
        }



    }

    @Test
    //测试异步调用
    public void test2() {
        ServiceProxy proxy = new ServiceProxy();
        proxy.setAsync();
        Calculator calculator = proxy.createProxy(Calculator.class);

        Integer[] results = new Integer[num];

        long start = System.currentTimeMillis();
        PromiseResponse[] promiseResponses = new PromiseResponse[num];
        for (int i = 0; i < num; i++) {
            calculator.add(0, i*2);
            promiseResponses[i] = rpcContext.getPromise();
        }

        for (int i = 0; i < num; i++) {
            results[i] = (Integer) promiseResponses[i].get(3000);
        }

        long end = System.currentTimeMillis();
        System.out.println(end - start);

        for (int result : results) {
            System.out.println(result);
        }


    }
}
