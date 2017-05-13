package kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

/**
 * Created by JesonLee
 * on 2017/5/12.
 */
public class KryoTest {
    String storePath = "J:\\Java\\projects\\learn-distubute\\nettybook2-master\\my-netty\\src\\main\\java\\rpc\\transport\\" + "test.txt";

    @Test
    public void test1() throws FileNotFoundException {
        Person person = Person.getPerson();
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Output output = new Output();
        output.setOutputStream(new FileOutputStream(storePath));
        kryo.writeClassAndObject(output, person);
    }
}

class Person implements Serializable {
    private String name;
    private int age;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    static Person getPerson() {
        Person person = new Person("Jeson", 19);
        return person;
    }
}