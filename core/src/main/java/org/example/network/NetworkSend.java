package org.example.network;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkSend {
    private final String destination;
    private final ByteBuffer[] buffers;

    public NetworkSend(String destination, ByteBuffer buffer) {
        this.destination = destination;
        this.buffers = new ByteBuffer[2];
        this.buffers[0] = sizeBuffer(buffer);
        this.buffers[1] = buffer;
    }

    private static ByteBuffer sizeBuffer(ByteBuffer buffer) {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(buffer.remaining());
        sizeBuffer.rewind();
        return sizeBuffer;
    }

    public long write(SocketChannel channel) {
        // TODO: 2021/7/16
        return 0;
    }
}
