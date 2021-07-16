package org.example.requests;

import org.example.network.AbstractRequest;
import org.example.protocol.ApiKeys;

public class JoinGroupRequest extends AbstractRequest {
    private String groupId;
    private String memberId;

    public JoinGroupRequest(short version, String groupId, String memberId) {
        super(ApiKeys.JOIN_GROUP, version);
        this.groupId = groupId;
        this.memberId = memberId;
    }
}
