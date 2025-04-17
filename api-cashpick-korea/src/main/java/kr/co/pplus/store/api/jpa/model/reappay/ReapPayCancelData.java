package kr.co.pplus.store.api.jpa.model.reappay;

import lombok.Data;


@Data
public class ReapPayCancelData {

    String payResponseCode;
    String payResponseMsg;
    String payResponsePayDate;
    String payResponsePayTime;
    String payResponseAmt;
    String payResponsePgSeq;
    String payResponseOrderNo;
    String payResponsePayType;
    String payResponseApprovalYMDHMS;
    String payResponseApprovalNo;
    String payResponseCardId;
    String payResponseCardNm;
    String payResponseSellMm;
    String payResponseZerofeeYn;
    String payResponseCertYn;
    String payResponseContractYn;
    String payResponsePartCancelFlag;
    String payResponseRemainAmt;
    String payResponseTestYn;
    String payResponseTranSeq;
    String payResponseInstallment;
    String payResponseProductType;
    String payResponseBizNo;


}
