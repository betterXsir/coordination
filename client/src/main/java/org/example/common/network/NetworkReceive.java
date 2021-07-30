package org.example.common.network;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkReceive {

    private String source;
    //收到的消息体的大小
    private ByteBuffer size;
    private ByteBuffer buffer;
    private int maxReceiveSize;
    private int receiveSize = -1;

    public NetworkReceive(String source, ByteBuffer buffer) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.buffer = buffer;
    }

    public NetworkReceive(String source, int maxReceiveSize) {
        this.source = source;
        this.size = ByteBuffer.allocate(4);
        this.maxReceiveSize = maxReceiveSize;
        this.buffer = null;
    }

    public long readFrom(SocketChannel channel) throws IOException {
        // 处理拆包、粘包问题
        // 读取消息体大小信息, 一次未读取完，下一次接着读取
        int totalRead = 0;
        if (size.hasRemaining()) {
            int bytesRead = channel.read(size);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            totalRead += bytesRead;
            if (!size.hasRemaining()) {
                //消息体大小信息读取完成
                size.flip();
                receiveSize = size.getInt();
                if (receiveSize < 0) {
                    throw new InvalidReceiveException("Invalid receive (size = " + receiveSize + ")");
                }
                if (maxReceiveSize > 0 && receiveSize > maxReceiveSize) {
                    throw new InvalidReceiveException("Invalid receive (size = " + receiveSize + " lager than " + maxReceiveSize + ")");
                }
            }
        }
        if (buffer != null && receiveSize != -1) {
            buffer = ByteBuffer.allocate(receiveSize);
        }
        if (buffer != null) {
            //读取消息体
            int bytesRead = channel.read(buffer);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            totalRead += bytesRead;
        }
        return totalRead;
    }

    public ByteBuffer payload() {
        return buffer;
    }

    public String getSource() {
        return source;
    }

    public boolean completed() {
        return !size.hasRemaining() && (buffer != null && !buffer.hasRemaining());
    }
}
