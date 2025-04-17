package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity(name="goodsRefDetail") // This tells Hibernate to make a table out of this class
@Table(name="goods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsRefDetail {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 순번',


    @Convert(converter = JpaConverterJson.class)
    @Column(name="attachments")
    Map<String, Object> attachments = null ;


    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    PageRefDetail page = null ; // '상품 상점 페이지 순번',

    @Key
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="category_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    GoodsCategory category = null ;

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name="goods_seq_no", insertable = false, updatable = false)
    @Where(clause = "type = 'thumbnail'")
    private List<GoodsImage> goodsImageList = new ArrayList<GoodsImage>();

    @Column(name="name")
    String name   = null ; //'상품명',

    @Column(name="type")
    Integer type   = null ; //'0 : 메뉴 주문 배달 상품, 1 : 일반 구매 상품

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_hotdeal")
    Boolean isHotdeal   = false ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_plus")
    Boolean isPlus   = false ;

    @Column(name="reward_luckybol")
    Integer rewardLuckybol   = 0 ;


    @Column(name="hashtag")
    String hashtag = null ; //'해쉬 태그',

    @Column(name="description")
    String description   = null ; //'상품 설명',

    @Column(name="count")
    Integer count  = -1 ; //'상품 수량  -1 : 수량제한 없음',

    @Column(name="sold_count")
    Long soldCount  = 0L ; //'팔린 상품 수량'

    @Column(name="status")
    Integer status   = 1 ; //'상품상태 1:판매중, 0:미판매, 2:판매종료',

    @Convert(converter = JpaConverterJson.class)
    @Column(name="goods_prop")
    Map<String, Object> goodsProp  = null ; //'상품 옵션',


    @Column(name="price")
    Float price  = null ; //'상품 실 판매 가격',

    @Column(name="origin_price")
    Float originPrice  = null ; //'상품 원 가격',


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_datetime")
    String expireDatetime  = null ; //'유효기간',

    @Column(name="expire_day")
    Integer expireDay  = null ; //'유효기간 구매 후 사용처리 기간',

    @Transient
    Long reviewCount = 0L ;

    @Transient
    Double avgEval = 0.0 ;

    @Transient
    Long likeCount = 0L ;

    @Transient
    Double distance = 0.0 ;

    @Transient
    Long pageCategorySeqNo = 0L ;

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

    @Column(name="register_type")
    String registerType  = null ;

    @Column(name="register")
    String register  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="blind")
    Boolean blind;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="represent")
    Boolean represent;

    @Column(name="note")
    String note  = null ;

    @Column(name="reason")
    String reason  = null ;

    @Column(name="service_condition")
    String serviceCondition  = null ;

    @Column(name="time_option")
    String timeOption  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_deleted")
    Boolean isDeleted;

    @Column(name="discount_ratio")
    Float discountRatio  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_coupon")
    Boolean isCoupon;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="all_days")
    Boolean allDays;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="all_weeks")
    Boolean allWeeks;

    @Column(name="day_of_weeks")
    String dayOfWeeks  = null ;

    @Column(name="sales_type")
    Long salesType; // 판매분류 - 매장판매(1), 배송(3), 배달, 예약

    @Column(name="is_packing")
    Boolean isPacking; // 포장 가능여부

    @Column(name="is_store")
    Boolean isStore; // 매장이용 가능여부

    @Column(name="delivery_fee")
    Integer deliveryFee; // 상품자체 배송비 (default -1 : page.deliveryFee 를 사용)

    @Column(name="refund_delivery_fee")
    Integer refundDeliveryFee; // 환불 배송비

    @Column(name="delivery_add_fee")
    Integer deliveryAddFee; // 추가 배송비 (default -1 : page.deliveryAddFee 사용)

    @Column(name="delivery_min_price")
    Integer deliveryMinPrice; // 추가 배송비 (default -1 : page.deliveryMinPrice 사용)

    @Column(name="reservation_min_number")
    Integer reservationMinNumber; // 최소예약인원

    @Column(name="reservation_max_number")
    Integer reservationMaxNumber; // 최대예약인원

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_delivery_plus")
    Boolean isDeliveryPlus   = false ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_delivery_hotdeal")
    Boolean isDeliveryHotdeal   = false ;

    @Column(name="external_url")
    String externalUrl  = null ;

    @Column(name="option_type")
    Integer optionType  = 0 ;

    @Column(name="buyable_count")
    Integer buyableCount  = 0 ;

    @Column(name="detail_type")
    String detailType  = null ;

    @Column(name="review_point")
    Integer reviewPoint  = 0 ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="recommend")
    Boolean recommend  = null ;

    @Column(name="sales_types")
    String salesTypes;

    @Column(name="market_type")
    String marketType;//retail, wholesale

    @Column(name="first")
    Long first = null ;

    @Column(name="second")
    Long second = null ;

    @Column(name="third")
    Long third = null ;

    @Column(name="wholesale_code")
    String wholesaleCode = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="first", insertable = false, updatable = false)
    CategoryFirst categoryFirst;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="second", insertable = false, updatable = false)
    CategorySecond categorySecond;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="third", insertable = false, updatable = false)
    CategoryThird categoryThird;
}
