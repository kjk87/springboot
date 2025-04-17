package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class ResTokenData {
    public String token;
    public long server_time;
    public long expired_at;
}
