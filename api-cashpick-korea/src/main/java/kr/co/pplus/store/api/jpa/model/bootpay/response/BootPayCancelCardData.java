package kr.co.pplus.store.api.jpa.model.bootpay.response;

import lombok.Data;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class BootPayCancelCardData {
    private String tid;
    private String card_approve_no;
    private String card_no;
    private String card_quota;
    private String card_company_code;
    private String card_company;
    private String receipt_url;
}
