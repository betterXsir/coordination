package org.example.protocol;

public class Field {
    public final String name;
    public final Type type;
    public final boolean hasDefaultValue;
    public final Object defaultValue;

    public Field(String name, Type type, boolean hasDefaultValue, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.hasDefaultValue = hasDefaultValue;
        this.defaultValue = defaultValue;
    }

    public Field(String name, Type type) {
        this(name, type, false, null);
    }

    public static void main(String[] args) {
        int a = -5;
        System.out.println("value: " + (long) a + ", binary: " + Long.toBinaryString((long) a));
    }
}
