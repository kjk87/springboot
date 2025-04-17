package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name = "productPriceWithDistance")
@Table(name = "product_price")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPriceWithDistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String code; // 상품코드
    @Column(name = "product_seq_no")
    private Long productSeqNo;
    @Column(name = "page_seq_no")
    private Long pageSeqNo;

    private Float price;

    // 소매
    @Column(name = "origin_price")
    private Float originPrice; //
    @Column(name = "is_discount")
    private Boolean isDiscount;
    private Float discount;
    @Column(name = "discount_unit")
    private String discountUnit; // percent, money

    // 도매
    @Column(name = "supply_price")
    private Float supplyPrice;
    @Column(name = "consumer_price")
    private Float consumerPrice;
    @Column(name = "maximum_price")
    private Float maximumPrice;

    @Column(name = "discount_ratio")
    Float discountRatio; // 할인율

    @Column(name = "is_luckyball")
    private Boolean isLuckyball;


    @Column(name = "market_type")
    private Integer marketType; // 1:도매상품, 2:소매상품, 3:가져온 상품


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    Integer status; //'상품상태 1:판매중, 0:판매완료 soldout, -1:판매종료(expire), -2:판매중지, -999: 삭제

    private Boolean pick;

    @Column(name = "is_point")
    private Boolean isPoint;

    private Float point;

    @Column(name = "point_unit")
    private String pointUnit;

    @Column(name = "point_price")
    private Float pointPrice;

    @Column(name = "product_type")
    private String productType;//상품유형 - lunch, dinner, time (ticket상품인 경우에 적용)

    @Column(name = "daily_count")
    private Integer dailyCount;

    @Convert(converter = JpaConverterTime.class)
    @Column(name="start_time")
    String startTime  = null ;

    @Convert(converter = JpaConverterTime.class)
    @Column(name="end_time")
    String endTime  = null ;

    @Column(name = "daily_sold_count")
    private Integer dailySoldCount;

    @Column(name = "unit_price")
    private Float unitPrice;

    private Integer times;

    @Column(name = "remain_days")
    private Integer remainDays;

    @Column(name = "is_subscription")
    private Boolean isSubscription;

    @Column(name = "is_prepayment")
    private Boolean isPrepayment;

    @Column(name = "product_name")
    private String productName;

    private String image;

    @Column(name = "price_url")
    private String priceUrl;

    @Column(name = "refund_cash")
    private Integer refundCash;

    @Column(name = "refund_bol")
    private Integer refundBol;

    @Column(name = "effective_date")
    private String effectiveDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Product product;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_seq_no", referencedColumnName = "product_seq_no", insertable = false, updatable = false)
    private ProductDelivery productDelivery;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;

    @Column(name="avg_eval")
    private Float avgEval;

    @Column(name="is_like")
    private Boolean isLike;

    Double distance;

}
