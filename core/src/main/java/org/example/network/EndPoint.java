package org.example.network;

import org.apache.commons.lang3.StringUtils;

public class EndPoint {
    private String address;
    private Integer port;

    public EndPoint(Integer port) {
        this.port = port;
    }

    public EndPoint(String address, Integer port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String toString() {
        return StringUtils.isEmpty(address) ? "localhost" : address + ":" + port;
    }
}
