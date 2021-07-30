package org.example.protocol;

public class BoundField {
    public Field def;
    final int index;
    final Schema schema;

    public BoundField(Field field, int index, Schema schema) {
        this.def = field;
        this.index = index;
        this.schema = schema;
    }
}
