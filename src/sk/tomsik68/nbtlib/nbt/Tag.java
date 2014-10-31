package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.logging.Logger;

public class Tag<T> {
    public static class Type {
        public static final byte TAG_End = 0;
        public static final byte TAG_Byte = 1;
        public static final byte TAG_Short = 2;
        public static final byte TAG_Int = 3;
        public static final byte TAG_Long = 4;
        public static final byte TAG_Float = 5;
        public static final byte TAG_Double = 6;
        public static final byte TAG_Byte_Array = 7;
        public static final byte TAG_String = 8;
        public static final byte TAG_List = 9;
        public static final byte TAG_Compound = 10;
        public static final byte TAG_Int_Array = 11;

        public static byte fromClass(Class<?> clazz) {
            if (clazz.equals(Integer.TYPE)) {
                return TAG_Int;
            } else if (clazz.equals(Byte.TYPE)) {
                return TAG_Byte;
            } else if (clazz.equals(byte[].class)) {
                return TAG_Byte_Array;
            } else if (clazz.equals(Double.TYPE)) {
                return TAG_Double;
            } else if (clazz.equals(Float.TYPE)) {
                return TAG_Float;
            } else if (clazz.equals(Long.TYPE)) {
                return TAG_Long;
            } else if (clazz.equals(Short.TYPE)) {
                return TAG_Short;
            } else if (clazz.equals(String.class)) {
                return TAG_String;
            } else if (clazz.equals(int[].class)) {
                return TAG_Int_Array;
            }
            return TAG_End;
        }

        public static Class<?> fromType(byte t) {
            switch (t) {
            case TAG_Byte:
                return Byte.TYPE;
            case TAG_Byte_Array:
                return byte[].class;
            case TAG_Double:
                return Double.TYPE;
            case TAG_Float:
                return Float.TYPE;
            case TAG_Int:
                return Integer.TYPE;
            case TAG_Long:
                return Long.TYPE;
            case TAG_Short:
                return Short.TYPE;
            case TAG_String:
                return String.class;
            case TAG_Int_Array:
                return int[].class;
            }
            return null;
        }
    }

    private byte type;
    private String name;
    protected T value;

    public Tag(byte type, String name) {
        setType(type);
        setName(name);
    }

    public Tag(byte type, String name, T value) {
        this(type, name);
        this.value = value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public final void write(DataOutputStream dos) {
        try {
            dos.writeByte(this.type);
            if (this.type != 0) {
                dos.writeUTF(this.name);
                writeValue(dos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeValue(DataOutputStream dos) throws Exception {
        if ((this.value instanceof Byte))
            dos.writeByte(((Byte) this.value).byteValue());
        else if ((this.value instanceof Short))
            dos.writeShort(((Short) this.value).shortValue());
        else if ((this.value instanceof Integer))
            dos.writeInt(((Integer) this.value).intValue());
        else if ((this.value instanceof Long))
            dos.writeLong(((Long) this.value).longValue());
        else if ((this.value instanceof Float))
            dos.writeFloat(((Float) this.value).floatValue());
        else if ((this.value instanceof Double))
            dos.writeDouble(((Double) this.value).doubleValue());
        else if ((this.value instanceof String)) {
            dos.writeUTF((String) value);
        } else
            throw new IllegalStateException();
    }

    @SuppressWarnings("unchecked")
    public final void read(DataInputStream dis) {
        if (dis == null)
            throw new NullPointerException("Where should I read from?!");
        try {
            this.type = dis.readByte();
            if (type != Type.TAG_End) {
                this.name = dis.readUTF();
                this.value = (T) readValue(dis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Object readValue(DataInputStream dis) throws Exception {
        Object result = null;
        switch (this.type) {
        case Type.TAG_Byte:
            result = dis.readByte();
            break;
        case Type.TAG_Double:
            result = dis.readDouble();
            break;
        case Type.TAG_Float:
            result = dis.readFloat();
            break;
        case Type.TAG_Int:
            result = dis.readInt();
            break;
        case Type.TAG_Long:
            result = dis.readLong();
            break;
        case Type.TAG_Short:
            result = dis.readShort();
            break;
        case Type.TAG_String:
            result = dis.readUTF();
            break;
        }
        return result;
    }

    public void log(Logger l) {
        l.info("Tag: Type=" + type + " Name=" + this.name + " value=" + this.value);
    }

    public static Tag<?> readStatic(DataInputStream dis) throws Exception {
        @SuppressWarnings("rawtypes")
        Tag tag;
        byte type = dis.readByte();
        switch (type) {
        case Type.TAG_Byte_Array:
            tag = new TagByteArray(dis.readUTF(), new byte[0]);
            break;
        case Type.TAG_Compound:
            tag = new TagCompound(dis.readUTF(), null);
            break;
        case Type.TAG_List:
            tag = new TagList(dis.readUTF(), null, Type.TAG_End);
            break;
        case Type.TAG_End:
            tag = new Tag<Object>(Type.TAG_End, "");
            return tag;
        case Type.TAG_Byte:
            tag = new Tag<Byte>(Type.TAG_Byte, dis.readUTF());
            break;
        case Type.TAG_Double:
            tag = new Tag<Double>(Type.TAG_Double, dis.readUTF());
            break;
        case Type.TAG_Float:
            tag = new Tag<Float>(Type.TAG_Float, dis.readUTF());
            break;
        case Type.TAG_Int:
            tag = new Tag<Integer>(Type.TAG_Int, dis.readUTF());
            break;
        case Type.TAG_Long:
            tag = new Tag<Long>(Type.TAG_Long, dis.readUTF());
            break;
        case Type.TAG_Short:
            tag = new Tag<Short>(Type.TAG_Short, dis.readUTF());
            break;
        case Type.TAG_String:
            tag = new Tag<String>(Type.TAG_String, dis.readUTF());
            break;
        case Type.TAG_Int_Array:
            tag = new TagIntArray(dis.readUTF());
        default:
            tag = null;
        }
        tag.setValue(tag.readValue(dis));
        return tag;
    }
}