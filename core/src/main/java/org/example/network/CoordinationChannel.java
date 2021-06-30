package org.example.network;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class CoordinationChannel {
    private final String id;
    private final int maxReceiveSize;
    private final SocketChannel socketChannel;
    private NetworkReceive receive;
    private SocketAddress remoteAddress;

    public CoordinationChannel(String id, int maxReceiveSize, SocketChannel socketChannel) {
        this.id = id;
        this.maxReceiveSize = maxReceiveSize;
        this.socketChannel = socketChannel;
    }
}
