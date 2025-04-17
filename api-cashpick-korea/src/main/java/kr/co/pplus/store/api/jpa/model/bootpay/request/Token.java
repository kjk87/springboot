package kr.co.pplus.store.api.jpa.model.bootpay.request;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class Token {
    public String application_id;
    public String private_key;
}
