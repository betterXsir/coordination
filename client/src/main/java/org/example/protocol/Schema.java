package org.example.protocol;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Schema extends Type{
    private BoundField[] fields;
    private Map<String, BoundField> fieldsByName = new HashMap<>();

    public Schema(Field... fs) {
        this.fields = new BoundField[fs.length];
        for (int i = 0; i < fs.length; i++) {
            BoundField boundField = new BoundField(fs[i], i, this);
            BoundField existed = fieldsByName.get(boundField.def.name);
            if (existed != null) {
                throw new SchemaException("Field " + boundField.def.name + " has been added.");
            }
            fieldsByName.put(boundField.def.name, boundField);
        }
    }

    @Override
    public int sizeOf(Object o) {
        return 0;
    }

    public int numFields() {
        return fields.length;
    }

    /**
     *
     * @param buffer
     * @param o 复合结构数据Struct
     */
    @Override
    public void write(ByteBuffer buffer, Object o) {
        Struct struct = (Struct) o;
        for (BoundField field : fields) {
            //根据域从数据中获取对应的值
            Object value = struct.getByField(field);
            field.def.type.write(buffer, value);
        }
    }

    @Override
    public Struct read(ByteBuffer buffer) {
        // TODO: 2021/7/21 反序列化
        return null;
    }

    @Override
    public Object validate(Object o) {
        return null;
    }

    public BoundField get(String fieldName) {
        return fieldsByName.get(fieldName);
    }
}
