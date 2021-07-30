package org.example.protocol;

public interface Message {
    /**
     * Reads this message from a Struct object
     * @param struct
     * @param version
     */
    void fromStruct(Struct struct, short version);

    /**
     * Writes out the message to a Struct
     * @param version
     * @return
     */
    Struct toStruct(short version);
}
