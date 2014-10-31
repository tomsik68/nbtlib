package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TagIntArray extends Tag<int[]> {

    public TagIntArray(String name) {
        this(name, new int[0]);
    }

    public TagIntArray(String name, int[] o) {
        super(Type.TAG_Int_Array, name, o);
    }

    public void append(int b) {
        int[] newarray = new int[this.value.length + 1];
        System.arraycopy(this.value, 0, newarray, 0, this.value.length);
        newarray[(this.value.length + 1)] = b;
        this.value = newarray;
    }

    public void removeLast() {
        int[] newarray = new int[this.value.length - 1];
        System.arraycopy(this.value, 0, newarray, 0, this.value.length - 1);
        this.value = newarray;
    }

    @Override
    public void writeValue(DataOutputStream dos) throws Exception {
        dos.writeInt(this.value.length);
        for (int b : this.value) {
            dos.writeInt(b);
        }
    }

    @Override
    protected Object readValue(DataInputStream dis) throws Exception {
        final int len = dis.readInt();
        final int[] buf = new int[len];
        for (int i = 0; i < len; i++) {
            buf[i] = dis.readInt();
        }
        return buf;
    }
}
