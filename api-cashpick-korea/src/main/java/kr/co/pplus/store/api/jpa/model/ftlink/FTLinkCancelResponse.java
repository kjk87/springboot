package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;

@Data
public class FTLinkCancelResponse {
    String errCode;
    String errMessage;

    String orderNo ; //주문번호

    String APPRTRXID;
}
