package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class PayJoaCancelReadyRequest {

    String CPID ;

    String PAYMETHOD ;

    String CANCELREQ ;
}
