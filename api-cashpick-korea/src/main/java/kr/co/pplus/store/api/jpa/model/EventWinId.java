package kr.co.pplus.store.api.jpa.model;

import java.io.Serializable;

public class EventWinId implements Serializable {

    private Long eventSeqNo;

    private Integer seqNo;

    public EventWinId() {
    }

    public EventWinId(Long eventSeqNo, Integer seqNo) {
        this.eventSeqNo = eventSeqNo;
        this.seqNo = seqNo;
    }
}