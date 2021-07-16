package org.example.network;


import java.util.ArrayList;
import java.util.List;

public class SocketServer {
    private Acceptor acceptor;
    private EndPoint endPoint;
    private int networkThreads;
    private int nextProcessorId = 0;

    public SocketServer(int port, int networkThreads) {
        endPoint = new EndPoint(port);
        acceptor = new Acceptor(endPoint);
        this.networkThreads = networkThreads;
    }

    public void startUp() {
        acceptor.init();
        addProcessors(acceptor);
        Thread thread = new Thread(acceptor, String.format("socket-acceptor-%s", endPoint));
        thread.start();
    }

    private void addProcessors(Acceptor acceptor) {
        List<Processor> processors = new ArrayList<>();
        for (int index = 1; index <= networkThreads; index++) {
            nextProcessorId += 1;
            processors.add(new Processor(nextProcessorId, 104857600, 20));
        }
        acceptor.addProcessors(processors);
    }

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer(8080, 3);
        socketServer.startUp();
    }
}
