package hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class TestHessian {

    String storePath = "J:\\Java\\projects\\myrpc\\src\\main\\java\\version1\\" + "test.txt";
    @Test
    public void test1() throws IOException {
        Person person = Person.getPerson();
        FileOutputStream os = new FileOutputStream(storePath);

        //Hessian的序列化输出
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(person);
        ho.writeObject(new Person("java", 123));

        FileInputStream stream = new FileInputStream(storePath);
        HessianInput input = new HessianInput(stream);
        Person p = (Person) input.readObject();
        Person p1 = (Person) input.readObject();
        System.out.println(p);
        System.out.println(p1);
    }
}
