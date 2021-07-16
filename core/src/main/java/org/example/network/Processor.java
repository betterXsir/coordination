package org.example.network;

import org.example.expection.CoordinationException;
import org.example.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import sun.nio.ch.Net;
import sun.nio.cs.ext.ISO2022_CN;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private Integer id;
    private Selector selector;

    private int requestMaxSize;

    private int nextConnectionIndex = 0;
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private LinkedHashMap<String, NetworkReceive> completedReceives;
    private LinkedBlockingQueue<Response> responseQueue;

    public Processor(int id, int requestMaxSize, int connectionQueueSize) {
        this.id = id;
        this.requestMaxSize = requestMaxSize;
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
            handleNewConnections();
            // TODO: 2021/6/26 读请求添加到CompletedReceives，服务端对请求的响应放入responseQueue，分别进行处理
            // selector need to select to get events you are interested
            processNetworkEvent();
            // TODO: 2021/7/16 处理接收到的完成请求
            processCompletedReceive();
        }
    }

    private void processCompletedReceive() {
        for (Map.Entry<String, NetworkReceive> entry : completedReceives.entrySet()) {
            NetworkReceive receive = entry.getValue();

            // TODO: 2021/7/16 响应放入对应processor的响应队列
        }
    }

    private void processNetworkEvent() {
        // TODO: 2021/6/26 selector select to get events and process
        try {
            int pollTimeout = newConnections.isEmpty() ? 300 : 0;
            int numReadyKeys = selector.select(pollTimeout);
            if (numReadyKeys > 0) {
                // TODO: 2021/6/26 OP_CONNECT,OP_READ,OP_WRITE 以及 异常情况的DISCONNECTED
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                // TODO: 2021/6/30 事件处理顺序
                for (SelectionKey key : readyKeys) {
                    // TODO: 2021/6/30 OP_CONNECT
                    //if channel is ready and has bytes to read from socket, and has no previous
                    //completed receive then read from it
                    CoordinationChannel coordinationChannel = (CoordinationChannel) key.attachment();
                    if (key.isReadable() && !completedReceives.containsKey(coordinationChannel.getId())) {
                        long readBytes = coordinationChannel.read();
                        if (readBytes > 0) {
                            NetworkReceive completeReceive = coordinationChannel.maybeCompleteReceive();
                            if (completeReceive != null) {
                                //一个连接同时只能处理一个完整的请求，这取决于客户端使用阻塞式的发送方式，
                                //客户端一个连接同时只能发送一个请求，响应后才能发送下一个
                                completedReceives.put(coordinationChannel.getId(), completeReceive);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Processor poll failed.", e);
        }
    }

    private void handleNewConnections() {
        // TODO: 2021/6/26 批量处理
        SocketChannel newConnection = newConnections.poll();
        if (newConnection == null) {
            return;
        }
        try {
            SelectionKey key = newConnection.register(selector, SelectionKey.OP_READ);
            registerChannel(newConnection, key, connectionId(newConnection.socket()));
        } catch (Throwable e) {
            //close socket to avoid a socket leak
            CoreUtils.swallow(newConnection::close, logger, Level.ERROR);
        }
    }

    private void registerChannel(SocketChannel channel, SelectionKey key, String connectionId) {
        CoordinationChannel coordinationChannel = new CoordinationChannel(connectionId, requestMaxSize, channel);
        key.attach(coordinationChannel);
    }

    private String connectionId(Socket socket) {
        String localHost = socket.getLocalAddress().getHostAddress();
        int localPort = socket.getLocalPort();
        String remoteHost = socket.getInetAddress().getHostAddress();
        int remotePort = socket.getPort();
        String connectionId = localHost + ":" + localPort + "-" + remoteHost + ":" + remotePort + "-" + nextConnectionIndex;
        nextConnectionIndex = nextConnectionIndex == Integer.MAX_VALUE ? 0 : nextConnectionIndex + 1;
        return connectionId;
    }

    private void handleCompletedReceives() {
        // TODO: 2021/6/26
    }
}
