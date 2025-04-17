package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Map;

@Entity(name="goodsReview") // This tells Hibernate to make a table out of this class
@Table(name="goods_review")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsReview {

    public GoodsReview(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 리뷰 순번',

    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo = null ; //'구매 사용자 순번',


    @Column(name="page_seq_no", updatable = false)
    Long pageSeqNo  = null ; // '상점페이지 순번',


    @Column(name="goods_seq_no", updatable = false)
    Long goodsSeqNo = null ; //'상품 순번',

    @Column(name="goods_price_seq_no", updatable = false)
    Long goodsPriceSeqNo = null ; //'상품 순번',

    @Column(name="buy_goods_seq_no", updatable = false)
    Long buyGoodsSeqNo = null ; //'구매 상품 순번',

    @Column(name="buy_seq_no", updatable = false)
    Long buySeqNo = null ; //'구매 상품 순번',

    @Column(name="event_seq_no", updatable = false)
    Long eventSeqNo = null ;

    @Column(name="event_win_seq_no", updatable = false)
    Integer eventWinSeqNo = null ;

    @Column(name="event_gift_seq_no", updatable = false)
    Long eventGiftSeqNo = null ;

    @Column(name="review")
    String review = null ; //'구매 상품 리뷰',

    @Column(name="review_reply")
    String reviewReply = null ; //'구매 상품 리뷰 답변',

    @Column(name="eval")
    Byte eval = 0 ; // '구매 상품 평가 점수:1-5',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="review_reply_date")
    String reviewReplyDate  = null ; //'답변시각',

    @Convert(converter = JpaConverterJson.class)
    @Column(name="attachments")
    Map<String, Object> attachments = null ;


}
