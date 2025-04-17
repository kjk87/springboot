package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class FTLinkPayDecideRequest {

    String shopcode ;

    String compcode ;

    String orderno ;

    String APPRTRXID;
}
