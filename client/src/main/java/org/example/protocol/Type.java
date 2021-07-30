package org.example.protocol;

import org.example.common.utls.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Type {
    /**
     * Write the typed object to the buffer
     *
     */
    public abstract void write(ByteBuffer buffer, Object o);

    /**
     * Read the typed object from the buffer
     *
     */
    public abstract Object read(ByteBuffer buffer);

    /**
     * Return the size of the object in bytes
     */
    public abstract int sizeOf(Object o);

    /**
     * Validate the object. If succeeded return its typed object.
     *
     */
    public abstract Object validate(Object o);

    /**
     * Check if the type supports null values
     * @return whether or not null is a valid value for the type implementation
     */
    public boolean isNullable() {
        return false;
    }

    public static Type BOOLEAN = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            if ((Boolean) o) {
                buffer.put((byte) 1);
            } else {
                buffer.put((byte) 0);
            }
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.get() != 0;
        }

        @Override
        public int sizeOf(Object o) {
            return 1;
        }

        @Override
        public Object validate(Object o) {
            if (o instanceof Boolean)
                return (Boolean) o;
            else
                throw new SchemaException(o + " is not a Boolean.");
        }
    };

    public static Type INT8 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.put((Byte) o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.get();
        }

        @Override
        public int sizeOf(Object o) {
            return 1;
        }

        @Override
        public Byte validate(Object o) {
            if (o instanceof Byte) {
                return (Byte) o;
            } else {
                throw new SchemaException(o + " is not a Byte.");
            }
        }
    };

    public static Type INT16 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.putShort((short)o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.getShort();
        }

        @Override
        public int sizeOf(Object o) {
            return 2;
        }

        @Override
        public Short validate(Object o) {
            if (o instanceof Short)
                return (Short) o;
            else
                throw new SchemaException(o + " is not a Short.");
        }
    };

    public static Type INT32 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.putInt((Integer) o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.getInt();
        }

        @Override
        public int sizeOf(Object o) {
            return 4;
        }

        @Override
        public Integer validate(Object o) {
            if (o instanceof Integer)
                return (Integer) o;
            else
                throw new SchemaException(o + " is not a Integer.");
        }
    };

    public static Type UNSIGNED_INT32 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.putInt((int)((long)o & 0xffffffff));
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.getInt() & 0xffffffff;
        }

        @Override
        public int sizeOf(Object o) {
            return 4;
        }

        @Override
        public Long validate(Object o) {
            if (o instanceof Long)
                return (Long) o;
            else
                throw new SchemaException(o + " is not a Long");
        }
    };

    public static Type INT64 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.putLong((Long)o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.getLong();
        }

        @Override
        public int sizeOf(Object o) {
            return 8;
        }

        @Override
        public Long validate(Object o) {
            if (o instanceof Long)
                return (Long) o;
            else
                throw new SchemaException(o + " is not a Long.");
        }
    };

    public static Type FLOAT64 = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            buffer.putDouble((Double) o);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            return buffer.getDouble();
        }

        @Override
        public int sizeOf(Object o) {
            return 8;
        }

        @Override
        public Double validate(Object o) {
            if (o instanceof Double)
                return (Double) o;
            else
                throw new SchemaException(o + " is not a Double.");
        }
    };

    public static Type STRING = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            byte[] bytes = ((String)o).getBytes(StandardCharsets.UTF_8);
            if (bytes.length > Short.MAX_VALUE)
                throw new SchemaException("String length " + bytes.length + " is larger than the maximum string length.");
            buffer.putShort((short) bytes.length);
            buffer.put(bytes);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            short length = buffer.getShort();
            if (length < 0)
                throw new SchemaException("String length " + length + " cannot be negative");
            if (length > buffer.remaining())
                throw new SchemaException("Error reading string of length " + length + ", only " + buffer.remaining() + " bytes available");
            String val = new String(buffer.array(), buffer.arrayOffset() + buffer.position(), length, StandardCharsets.UTF_8);
            return val;
        }

        @Override
        public int sizeOf(Object o) {
            return 2 + Utils.utf8Length((String)o);
        }

        @Override
        public String validate(Object o) {
            if (o instanceof String)
                return (String) o;
            else
                throw new SchemaException(o + " is not a String.");
        }
    };

    public static final Type BYTES = new Type() {
        @Override
        public void write(ByteBuffer buffer, Object o) {
            ByteBuffer arg = (ByteBuffer) o;
            int pos = arg.position();
            buffer.putInt(arg.remaining());
            buffer.put(arg);
            arg.position(pos);
        }

        @Override
        public Object read(ByteBuffer buffer) {
            int size = buffer.getInt();
            if (size < 0)
                throw new SchemaException("Bytes size " + size + " cannot be negative");
            if (size > buffer.remaining())
                throw new SchemaException("Error reading bytes of size " + size + ", only " + buffer.remaining() + " bytes available");

            ByteBuffer val = buffer.slice();
            val.limit(size);
            buffer.position(buffer.position() + size);
            return val;
        }

        @Override
        public int sizeOf(Object o) {
            ByteBuffer buffer = (ByteBuffer) o;
            return 4 + buffer.remaining();
        }

        @Override
        public ByteBuffer validate(Object item) {
            if (item instanceof ByteBuffer)
                return (ByteBuffer) item;
            else
                throw new SchemaException(item + " is not a java.nio.ByteBuffer.");
        }
    };
}
