package org.example.network;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private ArrayBlockingQueue<Request> requestQueue;
    private LinkedBlockingQueue<Response> responseQueue;

    public Processor(int connectionQueueSize, int maxQueueRequests) {
        newConnections = new ArrayBlockingQueue<SocketChannel>(connectionQueueSize);
        requestQueue = new ArrayBlockingQueue<Request>(maxQueueRequests);
        responseQueue = new LinkedBlockingQueue();
    }

    public boolean accept(SocketChannel connection) {
        return newConnections.offer(connection);
    }

    public void run() {
        while(true) {
            // TODO: 2021/6/20 处理新的连接、处理到达的请求、处理完成的响应
        }
    }
}
