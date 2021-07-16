package org.example.requests;

import org.example.protocol.Struct;

public class RequestHeader {
    private short requestApiKey;
    private short requestApiVersion;
    private int correlationId;
    private String clientId;

    public RequestHeader() {
        this.requestApiKey = (short) 0;
        this.requestApiVersion = (short) 0;
        this.correlationId = 0;
        this.clientId = "";
    }

    public Struct toStruct() {
        // TODO: 2021/7/16
        return null;
    }

    public void setRequestApiKey(short requestApiKey) {
        this.requestApiKey = requestApiKey;
    }

    public void setRequestApiVersion(short requestApiVersion) {
        this.requestApiVersion = requestApiVersion;
    }

    public void setCorrelationId(int correlationId) {
        this.correlationId = correlationId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
