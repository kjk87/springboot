package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class PayJoaCancelRequest {

    String CPID ;

    String TRXID ;

    String AMOUNT ;

    String CANCELREASON ;
}
