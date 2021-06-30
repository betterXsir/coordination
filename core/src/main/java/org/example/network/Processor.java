package org.example.network;

import org.example.expection.CoordinationException;
import org.example.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import sun.nio.cs.ext.ISO2022_CN;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private Integer id;
    private Selector selector;
    private int nextConnectionIndex = 0;
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private LinkedHashMap<String, NetworkReceive> completedReceives;
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
            int pollTimeout = newConnections.isEmpty() ? 300 : 0;
            try {
                int numReadyKeys = selector.select(pollTimeout);
                if (numReadyKeys > 0) {
                    // TODO: 2021/6/26 OP_CONNECT,OP_READ,OP_WRITE 以及 异常情况的DISCONNECTED
                    Set<SelectionKey> readyKeys = selector.selectedKeys();
                    // TODO: 2021/6/30 事件处理顺序
                    for (SelectionKey key : readyKeys) {
                        // TODO: 2021/6/30 OP_CONNECT
                        //if channel is ready and has bytes to read from socket, and has no previous
                        //completed receive then read from it
                        String connectionId = (String) key.attachment();
                        if (key.isReadable() && !completedReceives.containsKey(connectionId)) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            NetworkReceive receive = new NetworkReceive();
                            long readBytes = receive.readFrom(channel);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Processor poll failed.", e);
            }
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
            SelectionKey key = newConnection.register(selector, SelectionKey.OP_READ);
            key.attach(connectionId(newConnection.socket()));
        } catch (Throwable e) {
            //close socket to avoid a socket leak
            CoreUtils.swallow(newConnection::close, logger, Level.ERROR);
        }
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
