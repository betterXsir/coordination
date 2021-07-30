package org.example.common.network;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkSend {
    private final String destination;
    private final ByteBuffer[] buffers;
    private int remaining;

    public NetworkSend(String destination, ByteBuffer buffer) {
        this.destination = destination;
        this.buffers = new ByteBuffer[2];
        this.buffers[0] = sizeBuffer(buffer);
        this.buffers[1] = buffer;
        for (ByteBuffer bufferTemp : buffers) {
            remaining += bufferTemp.remaining();
        }
    }

    private static ByteBuffer sizeBuffer(ByteBuffer buffer) {
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.putInt(buffer.remaining());
        sizeBuffer.rewind();
        return sizeBuffer;
    }

    public boolean completed() {
        return remaining <= 0;
    }

    public long write(SocketChannel channel) throws IOException {
        // TODO: 2021/7/16
        long written = channel.write(buffers);
        if (written < 0)
            throw new EOFException("Wrote negative bytes to channel. This shouldn't happen.");
        remaining -= written;
        return written;
    }

    public String getDestination() {
        return destination;
    }
}
