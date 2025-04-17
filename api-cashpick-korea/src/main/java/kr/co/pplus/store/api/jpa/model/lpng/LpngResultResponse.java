package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;

@Data
public class LpngResultResponse {



    String returncode;
    String errormsg;

    String shopcode ;
    String orderno ;
    String orderstatus ;

    String cardno; // 카드번호
    String cardname; //매임사명
    String apprno; // 승인번호
    String tranno; //pg고유번호
    String paydate;
    String paytime ;
    String userno;
}
