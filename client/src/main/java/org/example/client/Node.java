package org.example.client;

public class Node {
    private final int id;
    private final String idString;
    private final String host;
    private final int port;

    public Node(int id, String host, int port) {
        this.id = id;
        this.idString = Integer.toString(id);
        this.host = host;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return idString;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
