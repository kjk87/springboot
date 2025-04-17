package kr.co.pplus.store.api.jpa.model;

import lombok.Data;


@Data
public class DaouCardRequest {

    String cardNo ;

    String expireDt ;

    String cardAuth ;

    String cardPassword ;
}
