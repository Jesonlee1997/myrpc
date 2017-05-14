package future2;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class Main {
    public static void main(String[] args) {
        Future future = new Future();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                future.setData("hello world");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println(future.get());
    }
}
