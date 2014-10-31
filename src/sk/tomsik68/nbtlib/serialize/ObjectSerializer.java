package sk.tomsik68.nbtlib.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.tomsik68.nbtlib.nbt.Tag;
import sk.tomsik68.nbtlib.nbt.Tag.Type;
import sk.tomsik68.nbtlib.nbt.TagByteArray;
import sk.tomsik68.nbtlib.nbt.TagCompound;
import sk.tomsik68.nbtlib.nbt.TagIntArray;
import sk.tomsik68.nbtlib.nbt.TagList;

public class ObjectSerializer {

    public TagCompound serialize(Object o) throws Exception {
        TagCompound tag = (TagCompound) objectToTag("", o);
        return tag;
    }

    private Tag<?> objectToTag(String name, Object o) throws Exception {
        Tag<?> result = new TagCompound(name, null);
        if (o == null) {
            ((TagCompound) result).setString(SerializeConstants.NULL, "");
            return result;
        }
        if (o instanceof Integer || Integer.TYPE.isInstance(o)) {
            result = new Tag<Integer>(Type.TAG_Int, name, (Integer) o);
        } else if (o instanceof Double || Double.TYPE.isInstance(o)) {
            result = new Tag<Double>(Type.TAG_Double, name, (Double) o);
        } else if (o instanceof Float || Float.TYPE.isInstance(o)) {
            result = new Tag<Float>(Type.TAG_Float, name, (Float) o);
        } else if (o instanceof String) {
            result = new Tag<String>(Type.TAG_String, name, o.toString());
        } else if (o instanceof Character || Character.TYPE.isInstance(o)) {
            result = new Tag<String>(Type.TAG_String, SerializeConstants.CHAR + name, "" + o);
        } else if (o instanceof byte[]) {
            result = new TagByteArray(name, (byte[]) o);
        } else if (o instanceof int[]) {
            result = new TagIntArray(name, (int[]) o);
        } else if (o instanceof Long || Long.TYPE.isInstance(o)) {
            result = new Tag<Long>(Type.TAG_Long, name, (Long) o);
        } else if (o instanceof Boolean || Boolean.TYPE.isInstance(o)) {
            result = new Tag<Byte>(Type.TAG_Byte, SerializeConstants.CHAR + name, (byte) (((Boolean) o) ? 1 : 0));
        } else if (o != null && o.getClass().isArray()) {
            result = new TagList(name, null, Type.TAG_Compound);
            for (int i = 0; i < Array.getLength(o); i++) {
                Object obj = Array.get(o, i);
                ((TagList) result).addTag(objectToTag("", obj));
            }
        } else {
            ((TagCompound) result).setString(SerializeConstants.CLASS, o.getClass().getName());
            List<Field> fields = getFieldsFrom(o.getClass());
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DontSave.class)) {
                    System.out.println("Serializing: " + field.getType().getSimpleName());
                    field.setAccessible(true);
                    ((TagCompound) result).addTag(objectToTag(field.getName(), field.get(o)));
                }
            }
        }
        return result;
    }

    private List<Field> getFieldsFrom(Class<? extends Object> class1) {
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> check = class1;
        while (check != null) {
            fields.addAll(Arrays.asList(check.getDeclaredFields()));
            check = check.getSuperclass();
        }
        return fields;
    }
}
