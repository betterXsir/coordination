package org.example.requests;

import org.example.protocol.ApiKeys;
import org.example.protocol.Struct;

public class RequestHeader {
    private short requestApiKey;
    private short requestApiVersion;
    private int correlationId;
    private String clientId;

    public RequestHeader(short requestApiKey, short requestApiVersion) {
        this.requestApiKey = requestApiKey;
        this.requestApiVersion = requestApiVersion;
    }

    public RequestHeader() {
        this.requestApiKey = (short) 0;
        this.requestApiVersion = (short) 0;
        this.correlationId = 0;
        this.clientId = "";
    }

    public ApiKeys apiKey() {
        return ApiKeys.forId(requestApiKey);
    }

    public Struct toStruct() {
        // TODO: 2021/7/16
        return null;
    }

    public RequestHeader setRequestApiKey(short requestApiKey) {
        this.requestApiKey = requestApiKey;
        return this;
    }

    public RequestHeader setRequestApiVersion(short requestApiVersion) {
        this.requestApiVersion = requestApiVersion;
        return this;
    }

    public short getRequestApiVersion() {
        return requestApiVersion;
    }

    public RequestHeader setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public RequestHeader setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }
}
