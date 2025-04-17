package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;

@Data
public class LpngResponseNew {



    String returncode;
    String errormsg;

    String shopcode ;
    String orderno ;
    String orderstatus ;
    String orderexpiredt ;

    String result_code;
//    String error_message;
//    String order_no;
//    String order_status;
//    String order_expire_dt;

    String errCode ;
//    String errMessage ;

    String comp_orderno ;                // 업체주문번호
    String comp_memno ;                // 업체멤버아이디 or 멤버번호
    String order_goodsname ;            // 주문 제품명
    String order_req_amt ;                // 요청금액
    String order_name ;                // 주문자명
    String order_hp ;                // 주문자 휴대폰
    String order_email ;                // 주문자 이메일
    String comp_temp1 ;
    String comp_temp2 ;
    String comp_temp3 ;
    String comp_temp4 ;
    String comp_temp5 ;

    String req_cardNo ;            // 카드번호
    String req_cardMonth ;            // (*) 카드유효기간 월( MM )
    String req_cardYear ;            // (*) 카드유효기간 년( YYYY )
    String req_installment ;            // 할부개월수

    String appr_no ;            // 승인번호
    String appr_tranNo ;            // PG 거래번호
    String appr_shopCode ;        // 상점코드
    String appr_date ;            // 결제일자
    String appr_time ;            // 결제시간
    String cardtxt ;                // 매입사명
}
