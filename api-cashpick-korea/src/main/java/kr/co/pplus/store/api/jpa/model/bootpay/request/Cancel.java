package kr.co.pplus.store.api.jpa.model.bootpay.request;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class Cancel {
    public String receipt_id;
    public Integer cancel_price;
    public String cancel_username;
    public String cancel_message;

}
