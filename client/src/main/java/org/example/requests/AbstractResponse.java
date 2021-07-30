package org.example.requests;

import org.example.common.network.NetworkSend;
import org.example.protocol.ApiKeys;
import org.example.protocol.Struct;

import java.nio.ByteBuffer;

public abstract class AbstractResponse {

    public NetworkSend toSend(String destination, short apiVersion) {
        return new NetworkSend(destination, serialize(apiVersion));
    }

    protected abstract Struct toStruct(short version);

    public ByteBuffer serialize(short version) {
        Struct bodyStruct = this.toStruct(version);
        ByteBuffer buffer = ByteBuffer.allocate(bodyStruct.sizeOf());
        bodyStruct.writeTo(buffer);
        return buffer;
    }

    public static AbstractResponse parseResponse(ApiKeys apiKey, Struct struct, short version) {
        switch (apiKey) {
            case JOIN_GROUP:
                return new JoinGroupResponse(struct, version);
            default:
                throw new AssertionError(String.format("ApiKey %s is not currently handled in `parseResponse`.", apiKey));
        }
    }
}
