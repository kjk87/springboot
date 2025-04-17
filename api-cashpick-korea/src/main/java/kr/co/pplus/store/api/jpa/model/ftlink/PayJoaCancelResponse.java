package kr.co.pplus.store.api.jpa.model.ftlink;

import lombok.Data;


@Data
public class PayJoaCancelResponse {

    String TOKEN;

    String RESULTCODE;

    String ERRORMESSAGE;

    String DAOUTRX;

    String AMOUNT;

    String CANCELDATE;
}
