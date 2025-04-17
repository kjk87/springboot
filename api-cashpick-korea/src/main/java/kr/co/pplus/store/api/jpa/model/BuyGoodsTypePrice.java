package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

@Data
public class BuyGoodsTypePrice {
    Float generalPrice ;
    Float hotdealPrice ;
    Float plusPrice ;
    Float totalPrice ;

    public BuyGoodsTypePrice(Float gPrice, Float hPrice, Float pPrice) {
        this.generalPrice = gPrice ;
        this.hotdealPrice = hPrice ;
        this.plusPrice = pPrice ;
        this.totalPrice = gPrice + hPrice + pPrice ;
    }
}
