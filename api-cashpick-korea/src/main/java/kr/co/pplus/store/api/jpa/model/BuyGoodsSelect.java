package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyGoodsSelect {

    Long goodsSeqNo = null ; // '구매 상품 순번',

    Long goodsPriceSeqNo = null;

    Integer count = 1 ; // '구매 상품 갯수',

    Integer deliveryFee = null;

    String receiverName  = null ;

    String receiverTel  = null ;

    String receiverPostCode  = null ;

    String receiverAddress  = null ;

    String deliveryMemo  = null ;

    private List<BuyGoodsOption> buyGoodsOptionSelectList = null;
}
