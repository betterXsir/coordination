package org.example.client;

import org.example.common.network.NetworkSend;
import org.example.requests.AbstractRequest;
import org.example.requests.AbstractResponse;
import org.example.requests.RequestHeader;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class InFlightRequests {
    private final int maxInFlightRequestsPerConnection;
    private final Map<String, Deque<InflightRequest>> requests = new HashMap<>();

    public InFlightRequests(int maxInFlightRequestsPerConnection) {
        this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
    }

    protected static class InflightRequest {
        private final String destination;
        private final RequestHeader requestHeader;
        private final AbstractRequest request;
        private final boolean expectResponse;
        private NetworkSend send;

        public InflightRequest(String destination, RequestHeader requestHeader, AbstractRequest request, boolean expectResponse, NetworkSend send) {
            this.destination = destination;
            this.requestHeader = requestHeader;
            this.request = request;
            this.expectResponse = expectResponse;
            this.send = send;
        }

        public String getDestination() {
            return destination;
        }

        public RequestHeader getRequestHeader (){
            return requestHeader;
        }

        public boolean isExpectResponse() {
            return expectResponse;
        }

        public ClientResponse completed(AbstractResponse response) {
            return new ClientResponse(destination, response);
        }
    }

    public void add(InflightRequest request) {
        String destination = request.getDestination();
        Deque<InflightRequest> queue = requests.get(destination);
        if (queue == null) {
            queue = new ArrayDeque<>();
            requests.put(destination, queue);
        }
        queue.addFirst(request);
    }

    public boolean canSendMore(String node) {
        //需要考虑上一次发送未完成的情况
        Deque<InflightRequest> queue = requests.get(node);
        return queue == null || queue.isEmpty() || (queue.peekFirst().send.completed() && queue.size() < maxInFlightRequestsPerConnection);
    }

    public InflightRequest lastSent(String node) {
        return requests.get(node).peekFirst();
    }

    public InflightRequest completeLastSent(String node) {
        return requests.get(node).pollFirst();
    }

    public InflightRequest completeNext(String node) {
        InflightRequest inflightRequest = requests.get(node).pollLast();
        return inflightRequest;
    }
}
