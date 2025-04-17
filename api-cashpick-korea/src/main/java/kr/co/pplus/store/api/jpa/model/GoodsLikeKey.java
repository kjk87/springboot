package kr.co.pplus.store.api.jpa.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class GoodsLikeKey implements Serializable {
    @Column(name="member_seq_no", nullable = false)
    Long memberSeqNo = null ; //'사용자 순번',

    @Column(name="page_seq_no", nullable = false)
    Long pageSeqNo = null  ; //'찜 상품 페이지 순번',

    @Column(name="goods_seq_no", nullable = false)
    Long goodsSeqNo = null ; //'찜 상품 순번'

}