package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TagByteArray extends Tag<byte[]> {
    public TagByteArray(String name, byte[] value) {
        super((byte) 7, name, value);
    }

    public void append(byte b) {
        byte[] newarray = new byte[this.value.length + 1];
        System.arraycopy(this.value, 0, newarray, 0, this.value.length);
        newarray[(this.value.length + 1)] = b;
        this.value = newarray;
    }

    public void removeLast() {
        byte[] newarray = new byte[this.value.length - 1];
        System.arraycopy(this.value, 0, newarray, 0, this.value.length - 1);
        this.value = newarray;
    }

    public void writeValue(DataOutputStream dos) throws Exception {
        dos.writeInt(this.value.length);
        for(byte b : this.value){
            dos.writeByte(b);
        }
    }

    protected Object readValue(DataInputStream dis) throws Exception {
        final int len = dis.readInt();
        final byte[] buf = new byte[len];
        for(int i = 0;i<len;i++){
            buf[i] = dis.readByte();
        }
        return buf;
    }
}