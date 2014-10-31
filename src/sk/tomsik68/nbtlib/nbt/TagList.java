package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;

public class TagList extends Tag<Tag<?>[]> {
    private byte lt;

    public TagList(String name, Tag<?>[] value, byte listType) {
        super((byte) 9, name, value != null ? value : new Tag[0]);
        lt = listType;
    }

    public byte getListType() {
        return lt;
    }

    public void addTag(Tag<?> tag) {
        if (tag.getType() != lt)
            throw new IllegalArgumentException("Tag type doesn't match!");
        Tag[] newarray = new Tag[value.length + 1];
        System.arraycopy(value, 0, newarray, 0, value.length);
        newarray[value.length] = tag;
        value = newarray;
    }

    public void writeValue(DataOutputStream dos) throws Exception {
        dos.writeByte(lt);
        dos.writeInt(value.length);
        for (Tag tag : value) {
            tag.writeValue(dos);
        }
    }

    @Override
    protected Object readValue(DataInputStream dis) throws Exception {
        lt = dis.readByte();
        int len = dis.readInt();
        for (int i = 0; i < len; i++) {
            Tag tag = null;
            switch (lt) {
            case Type.TAG_Byte_Array:
                tag = new TagByteArray(getName() + "[" + i + "]", new byte[0]);
                break;
            case Type.TAG_Compound:
                tag = new TagCompound(getName() + "[" + i + "]", null);
                break;
            case Type.TAG_List:
                tag = new TagList(getName() + "[" + i + "]", null, Type.TAG_End);
                break;
            case Type.TAG_End:
                tag = new Tag<Object>(Type.TAG_End, "ME IS END!");
                return tag;
            case Type.TAG_Byte:
                tag = new Tag<Byte>(Type.TAG_Byte, getName() + "[" + i + "]");
                break;
            case Type.TAG_Double:
                tag = new Tag<Double>(Type.TAG_Double, getName() + "[" + i + "]");
                break;
            case Type.TAG_Float:
                tag = new Tag<Float>(Type.TAG_Float, getName() + "[" + i + "]");
                break;
            case Type.TAG_Int:
                tag = new Tag<Integer>(Type.TAG_Int, getName() + "[" + i + "]");
                break;
            case Type.TAG_Long:
                tag = new Tag<Long>(Type.TAG_Long, getName() + "[" + i + "]");
                break;
            case Type.TAG_Short:
                tag = new Tag<Short>(Type.TAG_Short, getName() + "[" + i + "]");
                break;
            case Type.TAG_String:
                tag = new Tag<String>(Type.TAG_String, getName() + "[" + i + "]");
                break;
            }
            tag.setValue(tag.readValue(dis));
            addTag(tag);
        }
        return value;
    }

    @Override
    public void log(Logger l) {
        super.log(l);
        for (Tag t : value) {
            t.log(l);
        }
    }

    public int size() {
        return value.length;
    }
}