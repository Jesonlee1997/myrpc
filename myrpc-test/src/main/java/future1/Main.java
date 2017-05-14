package future1;

/**
 * Created by JesonLee
 * on 2017/5/14.
 */
public class Main {
    public static void main(String[] args) {
        Future<String> future = new Future<>();
        future.addListener(event -> System.out.println("操作完成"));
        System.out.println(future.get());
        future.setData("21312");
        System.out.println(future.get());
    }
}
