package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;

@Data
public class FTLinkCancelRequest {

    String shopcode ;

    String loginId ;

    String APPVERSION ;

    String SERVICECODE ;

    String orderNo ; //주문번호

    String tranNo ; //PG거래번호

    String cancelAmt; //취소 요청금액
}
