package org.example.network;

import org.apache.commons.lang3.StringUtils;
import org.example.expection.CoordinationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Acceptor implements Runnable {
    private EndPoint endPoint;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private List<Processor> processors;

    public Acceptor(EndPoint endPoint) {
        this.endPoint = endPoint;
        this.processors = new ArrayList<Processor>();
    }

    public void init() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new CoordinationException("Open selector failed.", e);
        }

        this.serverSocketChannel = openServerSocket(endPoint.getAddress(), endPoint.getPort());
    }

    public void addProcessors(List<Processor> newProcessors) {
        processors.addAll(newProcessors);
        // TODO: 2021/6/25 暂时不允许动态调整网络处理线程个数
        startProcessors(processors);
    }

    private void startProcessors(List<Processor> processors) {
        for(Processor processor : processors) {
            Thread thread = new Thread(processor, "coordination-network-thread-" + processor.getId());
            thread.start();
        }
    }

    /**
     * Accept loop that checks for new connection attempts
     */
    public void run() {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            int currentProcessorIndex = 0;
            while(true) {
                int ready = selector.select(500);
                if (ready > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while(iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        if (selectionKey.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            Processor processor = null;
                            do {
                                // TODO: 2021/6/25 如果所有的Processor都满负荷了需要block
                                processor = processors.get(currentProcessorIndex);
                                currentProcessorIndex ++;
                            } while (assignNewConnection(socketChannel, processor));
                        } else {
                            throw new IllegalArgumentException("Unrecognized key state for acceptor thread.");
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private boolean assignNewConnection(SocketChannel connection, Processor processor) {
        if (processor != null) {
            return processor.accept(connection);
        }
        return false;
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
