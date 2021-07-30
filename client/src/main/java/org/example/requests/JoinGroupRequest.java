package org.example.requests;

import org.example.protocol.*;


public class JoinGroupRequest extends AbstractRequest {
    private static final String GROUP_ID_KEY_NAME = "group_id";
    private static final String MEMBER_ID_KEY_NAME = "member_id";
    private static final String SESSION_TIMEOUT_KEY_NAME = "session_timeout_ms";
    private static final String REBALANCE_TIMEOUT_KEY_NAME = "session_timeout_ms";

    private String groupId;
    private String memberId;
    private int sessionTimeoutMs;
    private int rebalanceTimeoutMs;

    public JoinGroupRequest(ApiKeys api, short version, String groupId, String memberId, int sessionTimeoutMs, int rebalanceTimeoutMs) {
        super(api, version);
        this.groupId = groupId;
        this.memberId = memberId;
        this.sessionTimeoutMs = sessionTimeoutMs;
        this.rebalanceTimeoutMs = rebalanceTimeoutMs;
    }

    public static Schema SCHEMA_0 = new Schema(
            new Field("group_id", Type.STRING),
            new Field("member_id", Type.STRING),
            new Field("session_timeout_ms", Type.INT32),
            new Field("rebalance_timeout_ms", Type.INT32)
    );

    public static Schema[] SCHEMAS = new Schema[] {SCHEMA_0};

    @Override
    protected Struct toStruct() {
        Struct struct = new Struct(SCHEMA_0);
        struct.set(GROUP_ID_KEY_NAME, groupId);
        struct.set(MEMBER_ID_KEY_NAME, memberId);
        struct.set(SESSION_TIMEOUT_KEY_NAME, sessionTimeoutMs);
        struct.set(REBALANCE_TIMEOUT_KEY_NAME, rebalanceTimeoutMs);
        return struct;
    }
}
