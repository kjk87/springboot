package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

@Data
public class Price {
    Float price = null ;

    public Price(Float price){
        this.price = price ;
    }
}
