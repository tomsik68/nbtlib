package sk.tomsik68.nbtlib.serialize;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sk.tomsik68.nbtlib.nbt.TagCompound;

public class SerializeTest {
    static class A {
        public int x = -1;
        public int y = -5;
        public char c = '\\';
        public int[] test = new int[] {
                1, 2, 3, 4, 5, 6, 7, 8, 9
        };

        public A() {

        }
    }

    static class Test {
        public int num1 = 13546516;
        public String str = "asasd";
        // please note ALL kinds of arrays must be declared as Object[] or
        // you'll get an error
        private Object[] list = new A[] {
                new A(), new A()
        };

        public Test() {
        }
    }

    public static void main(String[] args) {
        try {
            // create Test object to serialize
            Test test = new Test();
            // Serialize object "Test"
            ObjectSerializer os = new ObjectSerializer();
            TagCompound tc = os.serialize(test);
            DataOutputStream dos;
            tc.write(dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(new File("serialize.nbt")))));
            dos.flush();
            dos.close();
            // Load serialized object
            ObjectLoader ol = new ObjectLoader();
            ol.addSampleInstance(ArrayList.class, new ObjectFactory() {
                @Override
                public Object createObject() {
                    return new ArrayList<Object>();
                }
            });
            test = (Test) ol.load(TagCompound.getTag(new DataInputStream(new GZIPInputStream(new FileInputStream(new File("serialize.nbt"))))));
            // Serialize loaded object again!
            tc = os.serialize(test);
            tc.write(dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(new File("serialize2.nbt")))));
            dos.flush();
            dos.close();
        } catch (StackOverflowError e) {
            // sometimes, the program runs into stack overflow...
            System.out.println("Stack Overflow!!!");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
