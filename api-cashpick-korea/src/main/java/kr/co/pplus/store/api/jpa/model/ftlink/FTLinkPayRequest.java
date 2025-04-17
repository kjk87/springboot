package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class FTLinkPayRequest {

    String shopcode ;

    String loginId ;

    String appversion ;

    String servicecode ;

    String autokey ;

    String order_req_amt; //결제요청금액

    String order_goodsname ; //주문상품명

    String order_name ; //주문자명

    String order_hp ; //주문자 휴대폰

    String order_email ;

    String req_installment ; //할부개월수

    String req_cardcode ; //카드사 코드

    String comp_orderno ; //업체주문번호(자체관리변호)

    String comp_memno ; //업체회원번호(자체회원번호)

    String comp_temp1 ; // 상품정보 담을예정

    String comp_temp2 ; // admin return 서버정보

    String comp_temp3 ; // 우리가 받을 플랫폼 수수료

    String comp_temp4 ;

    String comp_temp5 ;

    String serverType;

    String roomId;

    String reqdephold;

    String manual_used;

    String manual_amt;

    String ISTEST;

    String duptest;
}
