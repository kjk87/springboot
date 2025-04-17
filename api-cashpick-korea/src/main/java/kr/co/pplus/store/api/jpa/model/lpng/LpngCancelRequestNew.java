package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;

@Data
public class LpngCancelRequestNew {

    String shopcode;

    String servicecode;

    String order_req_amt; //취소 요청금액

    String orderno; //주문번호
}

