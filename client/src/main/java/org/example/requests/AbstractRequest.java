package org.example.requests;

import org.example.common.network.NetworkSend;
import org.example.protocol.ApiKeys;
import org.example.protocol.Struct;

import java.nio.ByteBuffer;

public abstract class AbstractRequest {
    private final ApiKeys api;
    private final short version;

    public AbstractRequest(ApiKeys api, short version) {
        this.api = api;
        this.version = version;
    }

    public ApiKeys getApi() {
        return api;
    }

    public short getVersion() {
        return version;
    }

    public NetworkSend toSend(String destination, RequestHeader header) {
        return new NetworkSend(destination, serialize(header));
    }

    protected abstract Struct toStruct();

    public ByteBuffer serialize(RequestHeader header) {
        // TODO: 2021/7/16
        Struct headerStruct = header.toStruct();
        Struct bodyStruct = this.toStruct();
        ByteBuffer buffer = ByteBuffer.allocate(headerStruct.sizeOf() + bodyStruct.sizeOf());
        headerStruct.writeTo(buffer);
        bodyStruct.writeTo(buffer);
        return buffer;
    }
}
