package org.example.protocol;

public enum ApiKeys {
    JOIN_GROUP(0, "JoinGroup"),
    HEARTBEAT(1, "Heartbeat"),
    LEAVE_GROUP(2, "LeaveGroup"),
    SYNC_GROUP(3, "SyncGroup");
    private int id;
    private String name;

    ApiKeys(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
