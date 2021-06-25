package org.example.network;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private Integer id;
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private LinkedBlockingQueue<Response> responseQueue;

    public Processor(int id, int connectionQueueSize) {
        this.id = id;
        newConnections = new ArrayBlockingQueue<SocketChannel>(connectionQueueSize);
        responseQueue = new LinkedBlockingQueue();
    }

    public Integer getId() {
        return id;
    }

    public boolean accept(SocketChannel connection) {
        return newConnections.offer(connection);
    }

    public void run() {
        while(true) {
            // TODO: 2021/6/20 处理新的连接、处理到达的请求、处理完成的响应
            SocketChannel newConnection = newConnections.poll();
        }
    }
}
