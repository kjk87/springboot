package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ResToken extends ResDefault {
    public ResTokenData data;
}
