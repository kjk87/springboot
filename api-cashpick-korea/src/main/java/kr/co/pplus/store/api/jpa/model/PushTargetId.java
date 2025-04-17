package kr.co.pplus.store.api.jpa.model;

import java.io.Serializable;

public class PushTargetId implements Serializable {

    private Long msgSeqNo;

    private Long memberSeqNo;


    public PushTargetId() {
    }

    public PushTargetId(Long msgSeqNo, Long memberSeqNo) {
        this.msgSeqNo = msgSeqNo;
        this.memberSeqNo = memberSeqNo;
    }
}