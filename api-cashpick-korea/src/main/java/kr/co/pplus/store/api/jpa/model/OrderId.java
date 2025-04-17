package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderId {
    String orderId =null ;

    public OrderId(String orderId){
        this.orderId = orderId ;
    }
}
