package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class LpngCancelRequest {

    String shopcode ;

    String loginId ;

    String APPVERSION ;

    String SERVICECODE ;

//    String receive_type ;
//
//    String receive_url ;


    String cancelAmt; //취소 요청금액

    String orderNo ; //주문번호

    String tranNo ; //PG거래번호
}
