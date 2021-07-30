package org.example.client;

import org.example.common.network.NetworkSend;
import org.example.common.network.NetworkReceive;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class CoordinationChannel {
    private final String id;
    private final int maxReceiveSize;
    private final SocketChannel socketChannel;
    private NetworkReceive receive;
    private SelectionKey key;
    private SocketAddress remoteAddress;
    private NetworkSend send;

    public CoordinationChannel(String id, int maxReceiveSize, SocketChannel socketChannel, SelectionKey key) {
        this.id = id;
        this.maxReceiveSize = maxReceiveSize;
        this.socketChannel = socketChannel;
        this.key = key;
        this.send = null;
    }

    public String getId() {
        return id;
    }

    public boolean ready() {
        return true;
    }

    public boolean finishConnect() throws IOException {
        boolean connected = socketChannel.finishConnect();
        if (connected)
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        return connected;
    }

    public long read() throws IOException {
        if (receive == null) {
            receive = new NetworkReceive(id, maxReceiveSize);
        }
        return receive.readFrom(socketChannel);
    }

    public long write() throws IOException {
        // TODO: 2021/7/27
        if (send != null)
            return send.write(socketChannel);

        return 0;
    }

    public NetworkSend maybeCompleteSend() {
        if (send != null && send.completed()) {
            //reset
            key.interestOps(key.interestOps() & ~ SelectionKey.OP_WRITE);
            NetworkSend result = send;
            send = null;
            return result;
        }
        return null;
    }

    public void setSend(NetworkSend send) {
        if (send != null) {
            throw new IllegalStateException("Attempt to begin a send operation with prior send operation still in progress, connection id is " + id);
        }
        this.send = send;
        this.key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    public boolean hasSend() {
        return send != null;
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
