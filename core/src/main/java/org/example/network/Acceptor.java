package org.example.network;

import org.apache.commons.lang3.StringUtils;
import org.example.expection.CoordinationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

public class Acceptor implements Runnable {
    private EndPoint endPoint;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private Processor processor;

    public Acceptor(EndPoint endPoint) {
        this.endPoint = endPoint;
    }

    private void init() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new CoordinationException("Open selector failed.", e);
        }

        this.serverSocketChannel = openServerSocket(endPoint.getAddress(), endPoint.getPort());
    }

    /**
     * Accept loop that checks for new connection attempts
     */
    public void run() {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            while(true) {
                int ready = selector.select(500);
                if (ready > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            // TODO: 2021/6/18 处理连接
                        } else {
                            throw new IllegalArgumentException("Unrecognized key state for acceptor thread.");
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Create a server socket to listen for connections on.
     */
    private ServerSocketChannel openServerSocket(String address, Integer port) {
        InetSocketAddress socketAddress = StringUtils.isEmpty(address) ? new InetSocketAddress(port) : new InetSocketAddress(address, port);
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(socketAddress);
            return serverSocketChannel;
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
