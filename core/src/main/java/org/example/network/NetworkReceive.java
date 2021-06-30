package org.example.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkReceive {
    private String source;
    private ByteBuffer size;
    private ByteBuffer buffer;

    public NetworkReceive(String source, ByteBuffer buffer) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.buffer = buffer;
    }

    public long readFrom(SocketChannel channel) {
        // TODO: 2021/6/22 read data from channel
        return 0;
    }
}
