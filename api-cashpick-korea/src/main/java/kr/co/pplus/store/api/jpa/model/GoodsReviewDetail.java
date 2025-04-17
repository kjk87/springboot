package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Map;

@Entity(name="goodsReviewDetail") // This tells Hibernate to make a table out of this class
@Table(name="goods_review")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsReviewDetail {


    public GoodsReviewDetail(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 리뷰 순번',


    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no")
    Member member = null ; //'구매 사용자 순번',


    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    PageRefDetail page  = null ; // '상점페이지 순번',


    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "goods_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Goods goods = null ; //'구매 상품 순번',

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "goods_price_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    GoodsPriceRef goodsPrice = null ; //'구매 상품 순번',

    @Column(name="buy_goods_seq_no", updatable = false)
    Long buyGoodsSeqNo = null ; //'구매 상품 순번',

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "buy_seq_no",  referencedColumnName="seq_no")
    Buy buy = null ; //'구매 상품 순번',


    @Column(name="review")
    String review = null ; //'구매 상품 리뷰',

    @Column(name="review_reply")
    String reviewReply = null ; //'구매 상품 리뷰 답변',

    @Column(name="eval")
    Integer eval = 0 ; // '구매 상품 평가 점수:1-5',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="review_reply_date")
    String reviewReplyDate  = null ; //'답변시각',

    @Column(name="goods_price_seq_no", updatable = false)
    Long goodsPriceSeqNo = null ; //'상품 순번',

    @Convert(converter = JpaConverterJson.class)
    @Column(name="attachments")
    Map<String, Object> attachments = null ;
}
