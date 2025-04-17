package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;



@Data
public class LpngRequestNew {

    String shopcode ;
    String servicecode ;
    String order_req_amt; //결제요청금액
    String order_goodsname ; //주문상품명
    String order_name ; //주문자명
    String order_hp ; //주문자 휴대폰
    String order_email ;
    String comp_orderno ; //업체주문번호(자체관리변호)
    String comp_memno ; //업체회원번호(자체회원번호)
    String req_install ; //할부개월수
    String callback_url;//결과값 받을 url주소

}
