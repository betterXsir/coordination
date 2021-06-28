package org.example.network;

import org.example.expection.CoordinationException;
import org.example.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private Integer id;
    private Selector selector;
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private LinkedBlockingQueue<Response> responseQueue;

    public Processor(int id, int connectionQueueSize) {
        this.id = id;
        newConnections = new ArrayBlockingQueue<SocketChannel>(connectionQueueSize);
        responseQueue = new LinkedBlockingQueue();
        try {
            selector = Selector.open();
        } catch (IOException e) {
            throw new CoordinationException(e);
        }
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
            handleNewConnections();
            // TODO: 2021/6/26 selector select to get events and process
            // TODO: 2021/6/26 OP_CONNECT,OP_READ,OP_WRITE 以及 异常情况的DISCONNECTED
            // TODO: 2021/6/26 读请求添加到CompletedReceives，服务端对请求的响应放入responseQueue，分别进行处理
            // selector need to select to get events you are interested
        }
    }

    private void handleNewConnections() {
        // TODO: 2021/6/26 批量处理
        SocketChannel newConnection = newConnections.poll();
        if (newConnection == null) {
            return;
        }
        try {
            newConnection.register(selector, SelectionKey.OP_READ);
        } catch (Throwable e) {
            //close socket to avoid a socket leak
            CoreUtils.swallow(newConnection::close, logger, Level.ERROR);
        }
    }

    private void handleCompletedReceives() {
        // TODO: 2021/6/26
    }
}
