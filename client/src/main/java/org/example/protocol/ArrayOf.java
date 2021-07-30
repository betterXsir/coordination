package org.example.protocol;


import java.nio.ByteBuffer;

public class ArrayOf extends Type {
    private final Type type;

    public ArrayOf(Type type) {
        this.type = type;
    }

    @Override
    public void write(ByteBuffer buffer, Object o) {
        if (o == null) {
            buffer.putInt(-1);
            return;
        }
        Object[] objs = (Object[])o;
        int size = objs.length;
        buffer.putInt(size);

        for (Object obj : objs) {
            type.write(buffer, obj);
        }
    }

    @Override
    public Object read(ByteBuffer buffer) {
        int size = buffer.getInt();
        if (size < 0)
            return null;
        Object[] objs = new Object[size];
        for (int i = 0; i < size; i++) {
            objs[i] = type.read(buffer);
        }
        return objs;
    }

    @Override
    public int sizeOf(Object o) {
        int size = 4;
        if (o == null)
            return size;
        Object[] objs = (Object[]) o;
        for (Object obj : objs) {
            size += type.sizeOf(obj);
        }
        return size;
    }

    @Override
    public Object[] validate(Object o) {
        try {
            Object[] array = (Object[]) o;
            for (Object obj : array) {
                type.validate(obj);
            }
            return array;
        } catch (ClassCastException e) {
            throw new SchemaException("Not an Object[]");
        }
    }
}
