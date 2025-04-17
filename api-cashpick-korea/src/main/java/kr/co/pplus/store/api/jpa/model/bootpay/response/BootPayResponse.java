package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class BootPayResponse {
    private String receipt_id;
    private String order_id;
    private Integer price;
    private Integer tax_free;
    private Integer cancelled_price;
    private Integer cancelled_tax_free;
    private String order_name;
    private String company_name;
    private String gateway_url;
    private Boolean sandbox;
    private String pg;
    private String method;
    private String method_symbol;
    private String method_origin;
    private String method_origin_symbol;
    private String receipt_url;
    private String purchased_at;
    private String cancelled_at;
    private String requested_at;
    private String escrow_status_locale;
    private String escrow_status;
    private String status_locale;
    private Integer status;
    private BootPayCancelCardData card_data;

}
