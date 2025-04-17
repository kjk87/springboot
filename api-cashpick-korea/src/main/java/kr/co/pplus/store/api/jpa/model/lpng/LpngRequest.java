package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;



@Data
public class LpngRequest {

    String shopcode ;

    String loginId ;

    String APPVERSION ;

    String SERVICECODE ;

    String receive_type ;

    String receive_url ;

    String order_req_amt; //결제요청금액

    String order_goodsname ; //주문상품명

    String order_name ; //주문자명

    String order_hp ; //주문자 휴대폰

    String order_email ;

    String req_cardNo ; // 카드번호

    String req_cardMonth ; //카드유효기간 월( MM )

    String req_cardYear ; //카드유효기간 년( YYYY )

    String req_identity ; //생년월일6자리(YYMMDD) or 사업자번호10자리

    String req_cardPwd ; //카드비밀번호 앞2자리

    String req_installment ; //할부개월수

    String comp_orderno ; //업체주문번호(자체관리변호)

    String comp_memno ; //업체회원번호(자체회원번호)

    String comp_temp1 ;

    String comp_temp2 ;

    String comp_temp3 ;

    String comp_temp4 ;

    String comp_temp5 ;
}
