package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;


@Data
public class LpngCheckRequest {

    String shopcode ;
    String servicecode ;
    String orderno ;//주문번호
    String order_req_amt; //결제요청금액

}
