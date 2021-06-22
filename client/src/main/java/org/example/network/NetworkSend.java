package org.example.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkSend {
    private String destination;
    private ByteBuffer buffer;

    public long writeTo(SocketChannel channel) {
        // TODO: 2021/6/22 write data to channel
        return 0;
    }
}
