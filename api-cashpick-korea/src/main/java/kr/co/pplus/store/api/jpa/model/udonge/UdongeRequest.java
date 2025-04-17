package kr.co.pplus.store.api.jpa.model.udonge;

import lombok.Data;


@Data
public class UdongeRequest {

    String gubun ;//NP : NFC, AP : 앱결제, VP : 사용처리, CP : 취소

    String userid ;//유저 id

    String ga_userid ;//비즈id

    String tr_num ;//승인번호

    String ord_num ;//주문번호

    String amount ;//금액

    String trdate; //결제일
}
