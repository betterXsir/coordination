package org.example.network;

import org.example.protocol.ApiKeys;
import org.example.requests.RequestHeader;

import java.nio.ByteBuffer;

public class AbstractRequest {
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

    public ByteBuffer serialize(RequestHeader header) {
        // TODO: 2021/7/16
        return null;
    }
}
