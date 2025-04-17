package kr.co.pplus.store.api.jpa.model.bootpay.request;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class SubscribeBilling {
    public String billing_key;
    public String item_name;
    public long price;
    public String order_id;
}
