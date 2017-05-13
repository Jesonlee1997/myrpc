package me.jesonlee.rpc.server;

/**
 * Created by JesonLee
 * on 2017/5/13.
 */
public class Run {
    public static void main(String[] args) {
        //ApplicationContext context = new ClassPathXmlApplicationContext("version1/rpc-server.xml");
        //TODO:注册服务
        try {
            new RpcProvider().startServer(8080);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
