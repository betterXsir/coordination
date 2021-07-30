package org.example.client;

import org.example.requests.AbstractRequest;
import org.example.requests.RequestHeader;

public class ClientRequest {
    private String destination;
    private AbstractRequest request;
    private final int correlationId;
    private final String clientId;
    private final boolean expectedResponse;

    public ClientRequest(String destination, AbstractRequest request, int correlationId, String clientId, boolean expectedResponse) {
        this.destination = destination;
        this.request = request;
        this.correlationId = correlationId;
        this.clientId = clientId;
        this.expectedResponse = expectedResponse;
    }

    public String getDestination() {
        return destination;
    }

    public AbstractRequest getRequest() {
        return request;
    }

    public boolean isExpectedResponse() {
        return expectedResponse;
    }

    public RequestHeader makerHeader(short version) {
        short apiKeyId = request.getApi().getId();
        return new RequestHeader(apiKeyId, version)
                .setClientId(clientId)
                .setCorrelationId(correlationId);
    }
}
