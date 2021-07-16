package org.example.network;

import java.io.IOException;
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

    public String getId() {
        return id;
    }

    public long read() throws IOException {
        if (receive == null) {
            receive = new NetworkReceive(id, maxReceiveSize);
        }
        return receive.readFrom(socketChannel);
    }

    public NetworkReceive maybeCompleteReceive() {
        if (receive != null){
            receive.payload().rewind();
            NetworkReceive result = receive;
            receive = null;
            return result;
        }
        return null;
    }
}
