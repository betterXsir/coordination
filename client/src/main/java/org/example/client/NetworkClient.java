package org.example.client;


import org.example.common.network.NetworkReceive;
import org.example.common.network.NetworkSend;
import org.example.protocol.Struct;
import org.example.requests.AbstractResponse;
import org.example.requests.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NetworkClient {
    private final Logger log = LoggerFactory.getLogger(NetworkClient.class);

    private Selector selector;

    /* the socket send buffer size in bytes */
    private final int socketSendBuffer;

    /* the socket receive size buffer in bytes */
    private final int socketReceiveBuffer;

    private InFlightRequests inFlightRequests;

    public NetworkClient(Selector selector, int socketReceiveBuffer, int socketSendBuffer, int maxInFlightRequestsPerConnection) {
        this.selector = selector;
        this.socketReceiveBuffer = socketReceiveBuffer;
        this.socketSendBuffer = socketSendBuffer;
        this.inFlightRequests = new InFlightRequests(maxInFlightRequestsPerConnection);
    }

    public boolean ready(Node node) throws IOException {
        if (isReady(node))
            return true;

        InetSocketAddress address = new InetSocketAddress(node.getHost(), node.getPort());
        selector.connect(node.getIdString(), address, socketSendBuffer, socketReceiveBuffer);

        return false;
    }

    public void send(ClientRequest request) {
        // TODO: 2021/7/23 异步发送，已发送请求进入队列，不阻塞在等待收到响应
        String destination = request.getDestination();
        RequestHeader header = request.makerHeader((short)0);
        NetworkSend send = request.getRequest().toSend(destination, header);
        //放置在对应连接通道中，等待Selector在poll方法中处理
        InFlightRequests.InflightRequest inflightRequest = new InFlightRequests.InflightRequest(
                destination,
                header,
                request.getRequest(),
                request.isExpectedResponse(),
                send);
        inFlightRequests.add(inflightRequest);
        selector.send(send);
    }

    public void poll(int timeout) {
        try {
            selector.poll(timeout);
        } catch (IOException e) {
            log.error("Unexpected error during I/O");
        }
        List<ClientResponse> responses = new ArrayList<>();
        handleCompletedSends(responses);
        handleCompletedReceives(responses);
    }

    /**
     * handle any completed sends. In particular if no response is expected then complete request immediately
     * @param responses
     */
    void handleCompletedSends(List<ClientResponse> responses) {
        for (NetworkSend send : selector.getCompletedSends()) {
            InFlightRequests.InflightRequest request = inFlightRequests.lastSent(send.getDestination());
            if (!request.isExpectResponse()) {
                //不需要响应的请求
                inFlightRequests.completeLastSent(send.getDestination());
                responses.add(request.completed(null));
            }
        }
    }

    /**
     * Handle any completed receives and fill response list.
     * @param responses
     */
    void handleCompletedReceives(List<ClientResponse> responses) {
        for (NetworkReceive receive : selector.getCompletedReceives()) {
            String source = receive.getSource();
            InFlightRequests.InflightRequest request = inFlightRequests.completeNext(source);
            // TODO: 2021/7/29
            //  1.反序列化响应
            //  2.触发请求回调

            RequestHeader requestHeader = request.getRequestHeader();
            Struct responseBody = requestHeader.apiKey().parseResponse(requestHeader.getRequestApiVersion(), receive.payload());
            AbstractResponse response = AbstractResponse.parseResponse(requestHeader.apiKey(), responseBody, requestHeader.getRequestApiVersion());
            responses.add(request.completed(response));

        }
    }

    private boolean isReady(Node node) {
        return selector.isChannelReady(node.getIdString()) && inFlightRequests.canSendMore(node.getIdString());
    }
}
