package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTTest {
    public static void main(String[] args) {
        try {
            File file = new File("test.dat");

            // save a TAG_COMPOUND
            TagCompound tc = new TagCompound("root", null);

            tc.setInt("testint",50);
            tc.setString("teststring", "Hello World");
            tc.addTag(new TagIntArray("intArray", new int[]{1,2,3,4,5,6}));
            

            file.createNewFile();
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
            tc.write(dos);
            dos.flush();
            dos.close();

            DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(file)));
            TagCompound tc_out = TagCompound.getTag(dis);
            dis.close();

            int testint = tc_out.getInt("testint");
            String teststr = tc_out.getString("teststring");
            TagIntArray intArray = (TagIntArray) tc_out.findTagByName("intArray");
            int[] numbers = intArray.getValue();
            
            System.out.println(testint);
            System.out.println(teststr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
