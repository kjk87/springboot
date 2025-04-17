package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyOrderTypeCount {
    Integer payCount = null ;
    Integer wrapCount = null ;
    Integer deliveryCount = null ;

    public BuyOrderTypeCount(Integer payCount, Integer wrapCount, Integer deliveryCount){
        this.payCount = payCount;
        this.wrapCount = wrapCount;
        this.deliveryCount = deliveryCount;
    }
}
