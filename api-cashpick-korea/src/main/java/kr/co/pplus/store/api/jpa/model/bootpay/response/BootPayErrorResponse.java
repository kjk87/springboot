package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class BootPayErrorResponse {
    private String error_code;
    private String message;
}
