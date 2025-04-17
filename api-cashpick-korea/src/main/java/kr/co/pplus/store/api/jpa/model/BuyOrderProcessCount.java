package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyOrderProcessCount {
    Integer readyCount = null ;
    Integer ingCount = null ;
    Integer completeCount = null ;

    public BuyOrderProcessCount(Integer readyCount, Integer ingCount, Integer completeCount){
        this.readyCount = readyCount;
        this.ingCount = ingCount;
        this.completeCount = completeCount;
    }
}
