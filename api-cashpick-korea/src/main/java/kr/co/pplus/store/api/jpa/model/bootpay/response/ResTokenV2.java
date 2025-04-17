package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class ResTokenV2 {
    public String access_token;
    public long expire_in;
}
