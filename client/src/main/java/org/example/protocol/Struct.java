package org.example.protocol;

import java.nio.ByteBuffer;

/**
 * 复合结构
 */
public class Struct {
    private Schema schema;
    private Object[] values;

    public Struct(Schema schema) {
        this.schema = schema;
        this.values = new Object[schema.numFields()];
    }

    public Struct(Schema schema, Object[] values) {
        this.schema = schema;
        this.values = values;
    }

    public Struct set(String name, Object value) {
        BoundField field = schema.get(name);
        if (field == null) {
            throw new SchemaException("Unknown field: " + name);
        }
        values[field.index] = value;
        return this;
    }

    public Struct setByteArray(String name, byte[] value) {
        ByteBuffer buf = value == null ? null : ByteBuffer.wrap(value);
        return set(name, buf);
    }

    public Object get(String name) {
        BoundField field = schema.get(name);
        if (field == null) {
            throw new SchemaException("No such filed: " + name);
        }
        return getFieldValue(field);
    }

    private Object getFieldValue(BoundField field) {
        Object value = values[field.index];
        if (value != null)
            return value;
        else if (field.def.hasDefaultValue)
            return field.def.defaultValue;
        else if (field.def.type.isNullable())
            return null;
        else
            throw new SchemaException("Missing value for field " + field.def.name + " which has not default value.");
    }

    public Object getByField(BoundField field) {
        return getFieldValue(field);
    }

    public Short getShort(String name) {
        return (Short) get(name);
    }

    public Integer getInt(String name) {
        return (Integer) get(name);
    }

    public Long getLong(String name) {
        return (Long) get(name);
    }

    public String getString(String name) {
        return (String) get(name);
    }

    public Boolean getBoolean(String name) {
        return (Boolean) get(name);
    }

    public byte[] getByteArray(String name) {
        Object result = get(name);
        if (result instanceof byte[])
            return (byte[]) result;
        ByteBuffer buffer = (ByteBuffer) result;
        byte[] arr = new byte[buffer.remaining()];
        buffer.get(arr);
        buffer.flip();
        return arr;
    }

    public Object[] getArray(String name) {
        return (Object[]) get(name);
    }

    public int sizeOf() {
        return schema.sizeOf(this);
    }

    public void writeTo(ByteBuffer buffer) {
        this.schema.write(buffer, this);
    }
}
