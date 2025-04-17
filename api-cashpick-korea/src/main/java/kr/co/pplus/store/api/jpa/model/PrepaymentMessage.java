package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

@Data
public class PrepaymentMessage {
    private String pageSeqNo;
    private String seqNo;
    private String type;
}
