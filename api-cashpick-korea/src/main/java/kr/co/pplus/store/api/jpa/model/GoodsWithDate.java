package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Entity(name="goodsWithDate") // This tells Hibernate to make a table out of this class
@Table(name="goods")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsWithDate {


    public GoodsWithDate(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 순번',


    @Convert(converter = JpaConverterJson.class)
    @Column(name="attachments")
    Map<String, Object> attachments = null ;

    @Column(name="page_seq_no")
    Long pageSeqNo = null ; // '상품 상점 페이지 순번',

    @Column(name="category_seq_no")
    Long categorySeqNo = null ;

    @Column(name="name")
    String name   = null ; //'상품명',

    @Column(name="type")
    Integer type   = null ; //'0 : 메뉴 주문 배달 상품, 1 : 일반 구매 상품

    @Column(name="is_hotdeal")
    Boolean isHotdeal   = false ;

    @Column(name="is_plus")
    Boolean isPlus   = false ;

    @Column(name="reward_luckybol")
    Integer rewardLuckybol   = 0 ;


    @Column(name="hashtag")
    String hashtag = null ; //'해쉬 태그',

    @Column(name="description")
    String description   = null ; //'상품 설명',

    @Column(name="count")
    Long count  = -1L ; //'상품 수량  -1 : 수량제한 없음',

    @Column(name="sold_count")
    Long soldCount  = 0L; //'상품 수량  -1 : 수량제한 없음',

    @Column(name="status")
    Integer status   = 1 ; //'상품상태  1:판매중(sail), 0:완판(soldOut), -1:판매종료(expire)', -2: 판매중지(stop), -999(삭제)'

    @Convert(converter = JpaConverterJson.class)
    @Column(name="goods_prop")
    Map<String, Object> goodsProp  = null ; //'상품 옵션',


    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',


    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    @Column(name="price")
    Float price  = null ; //'상품 실 판매 가격',

    @Column(name="origin_price")
    Float originPrice  = null ; //'상품 원 가격',

    @Column(name="expire_datetime")
    Date expireDatetime  = null ; //'유효기간',

    @Column(name="expire_day")
    Integer expireDay  = null ; //'유효기간 구매 후 사용처리 기간',

    @Column(name="lang")
    String lang  = "ko" ; //'상품명 등록 언어',

    @Convert(converter = JpaConverterTime.class)
    @Column(name="start_time")
    String startTime  = null ; //'구매 시작 시각',

    @Convert(converter = JpaConverterTime.class)
    @Column(name="end_time")
    String endTime  = null ; //'구매 종료 시각',

    @Column(name="reward_pr_link")
    Integer rewardPrLink  = 0 ;

    @Column(name="reward_pr_review_link")
    Integer rewardPrReviewLink  = 0 ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="news_datetime")
    String newsDatetime  = null ; //최신 소식으로 등록 시각
}
