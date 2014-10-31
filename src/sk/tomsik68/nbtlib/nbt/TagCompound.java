package sk.tomsik68.nbtlib.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class TagCompound extends Tag<Tag<?>[]> {
    public TagCompound(String name, Tag<?>[] value) {
        super((byte) 10, name, value != null ? value : new Tag[0]);
    }

    public void addTag(Tag<?> tag) {
        int i = 0;
        for (Tag<?> t : value) {
            if (t.getType() == tag.getType() && Arrays.equals(t.getName().toCharArray(), tag.getName().toCharArray())) {
                value[i] = tag;
                return;
            }
            ++i;
        }
        Tag[] newarray = new Tag[this.value.length + 1];
        System.arraycopy(this.value, 0, newarray, 0, this.value.length);
        newarray[this.value.length] = tag;
        this.value = newarray;
    }

    public Tag<?> findTagByName(String name) {
        for (Tag t : (Tag[]) this.value) {
            if (t.getName().equals(name))
                return t;
        }
        return null;
    }

    public Tag<Integer> findIntTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 3))
                return t;
        return null;
    }

    public Tag<Short> findShortTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 2))
                return t;
        return null;
    }

    public Tag<Long> findLongTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 4))
                return t;
        return null;
    }

    public Tag<Float> findFloatTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 5))
                return t;
        return null;
    }

    public Tag<Double> findDoubleTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 6))
                return t;
        return null;
    }

    public Tag<Byte> findByteTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 1))
                return t;
        return null;
    }

    public TagByteArray findByteArrayTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 7))
                return (TagByteArray) t;
        return null;
    }

    public Tag<String> findStringTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 8))
                return t;
        return null;
    }

    public Tag<Tag<?>[]> findContainerTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if (((t.getName().equals(name)) && (t.getType() == 10)) || (t.getType() == 9))
                return t;
        return null;
    }

    public TagCompound findCompoundTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 10))
                return (TagCompound) t;
        return null;
    }

    public TagList findListTagByName(String name) {
        for (Tag t : (Tag[]) this.value)
            if ((t.getName().equals(name)) && (t.getType() == 9))
                return (TagList) t;
        return null;
    }

    protected Object readValue(DataInputStream dis) throws Exception {
        ArrayList<Tag> subtags = new ArrayList<Tag>();
        while (true) {
            Tag tag = Tag.readStatic(dis);
            if (tag.getType() != Type.TAG_End)
                subtags.add(tag);
            else
                break;
        }
        return subtags.toArray(new Tag[0]);
    }

    public void writeValue(DataOutputStream dos) throws Exception {
        for (Tag<?> t : this.value) {
            t.write(dos);
        }
        if (value.length > 0) {
            if (value[value.length - 1].getType() != Type.TAG_End)
                dos.writeByte(Type.TAG_End);
        }
    }

    public static TagCompound getTag(DataInputStream dis) {
        TagCompound tc = new TagCompound("", null);
        tc.read(dis);
        return tc;
    }

    public void setInt(String name, int val) {
        Tag<Integer> toAdd = new Tag<Integer>((byte) 3, name, Integer.valueOf(val));
        addTag(toAdd);
    }

    public void setShort(String name, short val) {
        Tag<Short> toAdd = new Tag<Short>((byte) 2, name, Short.valueOf(val));
        addTag(toAdd);
    }

    public void setByte(String name, byte val) {
        Tag<Byte> toAdd = new Tag<Byte>((byte) 1, name, Byte.valueOf(val));
        addTag(toAdd);
    }

    public void setLong(String name, long val) {
        Tag<Long> toAdd = new Tag<Long>((byte) 4, name, Long.valueOf(val));
        addTag(toAdd);
    }

    public void setDouble(String name, double val) {
        Tag<Double> toAdd = new Tag<Double>((byte) 6, name, Double.valueOf(val));
        addTag(toAdd);
    }

    public void setFloat(String name, float val) {
        Tag<Float> toAdd = new Tag<Float>((byte) 5, name, Float.valueOf(val));
        addTag(toAdd);
    }

    public void setByteArray(String name, byte[] val) {
        TagByteArray toAdd = new TagByteArray(name, val);
        addTag(toAdd);
    }

    public void setString(String name, String val) {
        Tag<String> toAdd = new Tag<String>((byte) 8, name, val);
        addTag(toAdd);
    }

    public byte getByte(String name) {
        return findByteTagByName(name).getValue().byteValue();
    }

    public short getShort(String name) {
        return findShortTagByName(name).getValue().shortValue();
    }

    public int getInt(String name) {
        return findIntTagByName(name).getValue().intValue();
    }

    public long getLong(String name) {
        return findLongTagByName(name).getValue().longValue();
    }

    public float getFloat(String name) {
        return findFloatTagByName(name).getValue().floatValue();
    }

    public double getDouble(String name) {
        return findDoubleTagByName(name).getValue().doubleValue();
    }

    public String getString(String name) {
        return findStringTagByName(name).getValue();
    }

    public byte[] getByteArray(String name) {
        return findByteArrayTagByName(name).getValue();
    }

    @Override
    public void log(Logger l) {
        super.log(l);
        for (Tag t : value) {
            t.log(l);
        }
    }
}