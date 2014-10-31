package sk.tomsik68.nbtlib.serialize;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sk.tomsik68.nbtlib.nbt.Tag;
import sk.tomsik68.nbtlib.nbt.Tag.Type;
import sk.tomsik68.nbtlib.nbt.TagCompound;
import sk.tomsik68.nbtlib.nbt.TagList;

public class ObjectLoader {
    private final HashMap<String, ObjectFactory> instances = new HashMap<String, ObjectFactory>();

    public void addSampleInstance(Class<?> c, ObjectFactory of) {
        instances.put(c.getName(), of);
    }

    public Object load(Tag<?> tag) throws Exception {
        if (tag.getType() == Type.TAG_Compound) {
            if(((TagCompound)tag).findStringTagByName(SerializeConstants.NULL) != null){
                return null;
            }
            Class<?> clazz = Class.forName(((TagCompound) tag).getString(SerializeConstants.CLASS));
            Object result;
            try {
                result = clazz.newInstance();
            } catch (InstantiationException e) {
                result = instances.get(clazz.getName()).createObject();
                if (result == null){
                    e.printStackTrace();
                    throw new NullPointerException(clazz.getName() + " needs to have sample instance registered.");
                }
            }
            List<Field> fields = getFieldsFrom(clazz);
            for (Field field : fields) {
                field.setAccessible(true);
                if (!Modifier.isFinal(field.getModifiers()) && !field.isAnnotationPresent(DontSave.class)) {
                    if (field.getType().equals(Boolean.TYPE) || field.getType().equals(Boolean.class) || field.getType().equals(Character.TYPE) || field.getType().equals(Character.class))
                        field.set(result, load(((TagCompound) tag).findTagByName(SerializeConstants.CHAR + field.getName())));
                    else
                        field.set(result, load(((TagCompound) tag).findTagByName(field.getName())));
                }
            }
            return result;
        } else if (tag.getType() == Type.TAG_List) {
            TagList list = (TagList) tag;
            Object[] array = new Object[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = load(list.getValue()[i]);
            }
            return array;
        } else if (tag.getType() == Type.TAG_String && tag.getName().startsWith(SerializeConstants.CHAR)) {
            return tag.getValue().toString().charAt(1);
        } else if (tag.getType() == Type.TAG_Byte && tag.getName().startsWith(SerializeConstants.CHAR)) {
            return ((Byte) tag.getValue()) == 1;
        } else
            return tag.getValue();
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
