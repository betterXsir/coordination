package org.example.requests;

import org.example.protocol.*;

import java.util.List;

public class JoinGroupResponse extends AbstractResponse {
    private short errorCode;
    private int generationId;
    private String protocolType;
    private String protocolName;
    private String leader;
    private String memberId;
    private List<JoinGroupResponseMember> members;

    private static final String ERROR_CODE_KEY = "error_code";
    private static final String GENERATION_ID_KEY = "generation_id";
    private static final String PROTOCOL_TYPE_KEY = "protocol_type";
    private static final String PROTOCOL_NAME_KEY = "protocol_name";
    private static final String LEADER_KEY = "leader";
    private static final String MEMBER_ID_KEY = "member_id";
    private static final String MEMBERS_KEY = "members";

    public JoinGroupResponse(Struct struct) {
        short latestVersion = (short) (SCHEMAS.length - 1);
        fromStruct(struct, latestVersion);
    }

    public JoinGroupResponse(Struct struct, short version) {
        fromStruct(struct, version);
    }

    public static final Schema SCHEMA_0 = new Schema(
            new Field(ERROR_CODE_KEY, Type.INT16),
            new Field(GENERATION_ID_KEY, Type.INT32),
            new Field(PROTOCOL_TYPE_KEY, Type.STRING),
            new Field(PROTOCOL_NAME_KEY, Type.STRING),
            new Field(LEADER_KEY, Type.STRING),
            new Field(MEMBER_ID_KEY, Type.STRING),
            new Field(MEMBERS_KEY, new ArrayOf(JoinGroupResponseMember.SCHEMA_0))
    );

    public static final Schema[] SCHEMAS = new Schema[]{};

    @Override
    protected Struct toStruct(short version) {
        Struct struct = new Struct(SCHEMAS[version]);
        struct.set(ERROR_CODE_KEY, errorCode);
        struct.set(GENERATION_ID_KEY, generationId);
        struct.set(PROTOCOL_TYPE_KEY, protocolType);
        struct.set(PROTOCOL_NAME_KEY, protocolName);
        struct.set(LEADER_KEY, leader);
        struct.set(MEMBER_ID_KEY, memberId);
        Struct[] nestedObjects = new Struct[members.size()];
        int i = 0;
        for (JoinGroupResponseMember member : members) {
            nestedObjects[i] = member.toStruct(version);
        }
        struct.set(MEMBERS_KEY, nestedObjects);
        return struct;
    }

    protected void fromStruct(Struct struct, short _version) {
        this.errorCode = struct.getShort(ERROR_CODE_KEY);
        this.generationId = struct.getInt(GENERATION_ID_KEY);
        this.protocolType = struct.getString(PROTOCOL_TYPE_KEY);
        this.protocolName = struct.getString(PROTOCOL_NAME_KEY);
        this.leader = struct.getString(LEADER_KEY);
        this.memberId = struct.getString(MEMBER_ID_KEY);
        Object[] objects = struct.getArray(MEMBERS_KEY);
        for (Object obj : objects) {
            members.add(new JoinGroupResponseMember((Struct) obj, _version));
        }
    }

    public static class JoinGroupResponseMember implements Message {
        private String memberId;
        private byte[] metadata;

        public static final Schema SCHEMA_0 = new Schema(
                new Field("member_id", Type.STRING),
                new Field("metadata", Type.BYTES)
        );

        public static final Schema[] SCHEMAS = new Schema[]{SCHEMA_0};

        public JoinGroupResponseMember(Struct struct, short _version) {
            fromStruct(struct, _version);
        }

        @Override
        public void fromStruct(Struct struct, short _version) {
            this.memberId = struct.getString("member_id");
            this.metadata = struct.getByteArray("metadata");
        }

        @Override
        public Struct toStruct(short version) {
            Struct struct = new Struct(SCHEMAS[version]);
            struct.set("member_id", memberId);
            struct.setByteArray("metadata", metadata);
            return struct;
        }
    }
}
