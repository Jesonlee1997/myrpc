package version1.rpc;

import java.lang.reflect.Method;

/**
 * Created by JesonLee
 * on 2017/5/11.
 */
public class TestReflect {
    public static void main(String[] args) {
        Method[] methods = TestReflect.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }

    private void test() {

    }

    private static void test1() {

    }


}
