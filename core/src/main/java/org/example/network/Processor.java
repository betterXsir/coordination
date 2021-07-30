package org.example.network;

import org.example.common.network.NetworkReceive;
import org.example.utils.CoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Processor implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);
    private Integer id;
    private Selector selector;

    private int requestMaxSize;

    private int nextConnectionIndex = 0;
    private ArrayBlockingQueue<SocketChannel> newConnections;
    private LinkedBlockingQueue<Response> responseQueue;

    public Processor(int id, int requestMaxSize, int connectionQueueSize) {
        this.id = id;
        this.requestMaxSize = requestMaxSize;
        newConnections = new ArrayBlockingQueue<SocketChannel>(connectionQueueSize);
        responseQueue = new LinkedBlockingQueue();
        this.selector = new Selector(requestMaxSize);
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
        for (Map.Entry<String, NetworkReceive> entry : selector.getCompletedReceives().entrySet()) {
            NetworkReceive receive = entry.getValue();

            // TODO: 2021/7/16 响应放入对应processor的响应队列
        }
    }

    private void processNetworkEvent() {
        // TODO: 2021/6/26 selector select to get events and process
        try {
            int pollTimeout = newConnections.isEmpty() ? 300 : 0;
            selector.poll(pollTimeout);
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
            selector.registerChannel(newConnection, SelectionKey.OP_READ, connectionId(newConnection.socket()));
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
