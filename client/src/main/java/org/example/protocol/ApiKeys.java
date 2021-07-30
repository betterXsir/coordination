package org.example.protocol;

import org.example.requests.JoinGroupRequest;
import org.example.requests.JoinGroupResponse;

import java.nio.ByteBuffer;

public enum ApiKeys {
    JOIN_GROUP(0, "JoinGroup", JoinGroupRequest.SCHEMAS, JoinGroupResponse.SCHEMAS);

    private static final ApiKeys[] ID_TO_TYPE;
    private static final int MIN_API_KEY = 0;
    private static final int MAX_API_KEY;

    public short id;
    public String name;
    public final Schema[] requestSchemas;
    public final Schema[] responseSchemas;

    static {
        int maxKey = -1;
        for (ApiKeys key : ApiKeys.values()) {
            maxKey = Math.max(key.id, maxKey);
        }
        ApiKeys[] idToType = new ApiKeys[maxKey + 1];
        for (ApiKeys key : ApiKeys.values()) {
            idToType[key.id] = key;
        }
        ID_TO_TYPE = idToType;
        MAX_API_KEY = maxKey;
    }

    ApiKeys(int id, String name, Schema[] requestSchemas, Schema[] responseSchemas) {
        this.id = (short) id;
        this.name = name;
        this.requestSchemas = requestSchemas;
        this.responseSchemas = responseSchemas;
    }

    public Struct parseResponse(short version, ByteBuffer buffer) {
        if (!isVersionSupported(version)) {
            throw new IllegalArgumentException("Invalid version for API key " + this + ": " + version);
        }
        Schema schema = requestSchemas[version];
        return schema.read(buffer);
    }

    public static ApiKeys forId(int id) {
        if (!hasId(id)) {
            throw new IllegalArgumentException("Unknown ApiKeys id " + id);
        }
        return ID_TO_TYPE[id];
    }

    public boolean isVersionSupported(short apiVersion) {
        return apiVersion >= oldestVersion() && apiVersion <= latestVersion();
    }

    public short latestVersion() {
        return (short) (requestSchemas.length - 1);
    }

    public short oldestVersion() {
        return 0;
    }

    private static boolean hasId(int id) {
        return id >= MIN_API_KEY && id <= MAX_API_KEY;
    }
}
