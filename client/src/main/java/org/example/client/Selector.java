package org.example.client;

import org.example.common.network.NetworkSend;
import org.example.expection.CoordinationException;
import org.example.common.network.NetworkReceive;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Selector {

    private int maxReceiveSize;
    private java.nio.channels.Selector nioSelector;

    private Map<String, CoordinationChannel> channels;
    private List<NetworkSend> completedSends;

    private LinkedHashMap<String, NetworkReceive> completedReceives;

    private Set<SelectionKey> immediateConnectedKeys;

    public Selector(int maxReceiveSize) {
        this.maxReceiveSize = maxReceiveSize;
        this.immediateConnectedKeys = new HashSet<>();
        try {
            this.nioSelector = java.nio.channels.Selector.open();
        } catch (IOException e) {
            throw new CoordinationException(e);
        }
    }

    public boolean isChannelReady(String id) {
        CoordinationChannel coordinationChannel = channels.get(id);
        return coordinationChannel != null && coordinationChannel.ready();
    }

    public void connect(String id, InetSocketAddress address, int socketSendBuffer, int socketReceiveBuffer) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        SelectionKey connectKey = null;
        boolean connectedImmediately = false;
        try {
            socketChannel.configureBlocking(false);
            Socket socket = socketChannel.socket();
            //长连接
            socket.setKeepAlive(true);
            if (socketSendBuffer != -1)
                socket.setSendBufferSize(socketReceiveBuffer);
            if (socketReceiveBuffer != -1)
                socket.setReceiveBufferSize(socketReceiveBuffer);
            socket.setTcpNoDelay(true);

            // TODO: 2021/7/23 如果不能立即连接成功，会马上返回false，后续必须通过事件通知的方式调用finishConnect()方法完成连接
            connectedImmediately = socketChannel.connect(address);
            connectKey = registerChannel(socketChannel, SelectionKey.OP_CONNECT, id);
            if (connectedImmediately) {
                immediateConnectedKeys.add(connectKey);
                //清除兴趣位
                connectKey.interestOps(0);
            }
        } catch (Exception e) {
            if (connectKey != null && connectedImmediately) {
                immediateConnectedKeys.remove(connectKey);
            }
            channels.remove(id);
            socketChannel.close();
        }
    }

    public void send(NetworkSend send) {
        String destination = send.getDestination();
        CoordinationChannel channel = channels.get(destination);
        channel.setSend(send);
    }

    public void poll(int timeout) throws IOException {
        // TODO: 2021/7/27
        int numReadyKeys = nioSelector.select(timeout);

        if (numReadyKeys > 0 || !immediateConnectedKeys.isEmpty()) {
            Set<SelectionKey> readyKeys = nioSelector.selectedKeys();
            // TODO: 2021/6/30 事件处理顺序
            pollSelectedKeys(readyKeys, false);
            readyKeys.clear();

            pollSelectedKeys(immediateConnectedKeys, true);
            immediateConnectedKeys.clear();
        }
    }

    protected void pollSelectedKeys(Set<SelectionKey> selectionKeys, boolean immediateConnected) throws IOException{
        for (SelectionKey key : selectionKeys) {
            CoordinationChannel coordinationChannel = (CoordinationChannel) key.attachment();
            if (immediateConnected || key.isConnectable()) {
                //进行实际的连接，并注册读事件监听
                if (!coordinationChannel.finishConnect())
                    continue;
            }

            //if channel is ready and has bytes to read from socket, and has no previous
            //completed receive then read from it
            if (key.isReadable() && !completedReceives.containsKey(coordinationChannel.getId())) {
                long readBytes = coordinationChannel.read();
                if (readBytes > 0) {
                    NetworkReceive completeReceive = coordinationChannel.maybeCompleteReceive();
                    if (completeReceive != null) {
                        //一个连接同时只能处理一个完整的请求，这取决于客户端使用阻塞式的发送方式，
                        //客户端一个连接同时只能发送一个请求，响应后才能发送下一个
                        completedReceives.put(coordinationChannel.getId(), completeReceive);
                    }
                }
            }

            // TODO: 2021/7/27 attempt write
            if (coordinationChannel.hasSend() && coordinationChannel.ready() && key.isWritable()) {
                long written = coordinationChannel.write();
                //如果写入没有完成，下次会继续
                NetworkSend send = coordinationChannel.maybeCompleteSend();
                if (written > 0 && send != null) {
                    this.completedSends.add(send);
                }
            }
        }
    }

    public SelectionKey registerChannel(SocketChannel channel, int interestedOps, String connectionId) throws IOException {
        SelectionKey selectionKey = channel.register(nioSelector, interestedOps);
        CoordinationChannel coordinationChannel = new CoordinationChannel(connectionId, maxReceiveSize, channel, selectionKey);
        selectionKey.attach(coordinationChannel);

        channels.put(connectionId, coordinationChannel);
        return selectionKey;
    }

    public Collection<NetworkReceive> getCompletedReceives() {
        return completedReceives.values();
    }

    public List<NetworkSend> getCompletedSends() {
        return completedSends;
    }
}

