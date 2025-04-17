package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;

@Data
public class FTLinkCancelNotiRequest {

    String TOKEN;

    String RESULTCODE;

    String ERRORMESSAGE;

    String DAOUTRX;

    String AMOUNT;

    String CANCELDATE;

    String SHOPCODE;//shopcode

    String ORDERNO;//피플러스 주문번호

    String APPRDATE;//승인날짜

    String APPRTIME;//승인시간

    String APPRTRXID;//트렌젝션번호

    String APPRNO;//승인번호

    String PAYAMOUNT;//결제금액

    String pgcode; //다우-30, 다날-20

}
