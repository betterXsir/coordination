package org.example.client;

import org.example.requests.AbstractResponse;

public class ClientResponse {
    private final String destination;
    private final AbstractResponse response;

    public ClientResponse(String destination, AbstractResponse response) {
        this.destination = destination;
        this.response = response;
    }

    public void onComplete() {
        // TODO: 2021/7/29
    }
}
