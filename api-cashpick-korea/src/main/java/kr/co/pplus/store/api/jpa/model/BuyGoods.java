package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.*;

@Entity(name="buyGoods") // This tells Hibernate to make a table out of this class
@Table(name="buy_goods")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyGoods {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '구매 순번',

    @Key
    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo  = null ; // '사용자 순번',

    @Key
    @Column(name="page_seq_no", updatable = false)
    Long pageSeqNo  = null ; // '상점페이지 순번',

    @Key
    @Column(name="goods_seq_no", updatable = false)
    Long goodsSeqNo = null ; // '구매 상품 순번',

    @Column(name="count")
    Integer count = 1 ; // '구매 상품 갯수',


    @Convert(converter = JpaConverterJson.class)
    @Column(name="goods_prop")
    Map<String, Object> goodsProp  = null ; // '구매 상품 옵션',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; // '장바구니 상품 등록 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; // '장바구니 상품 변경 시각',

    @Key
    @Column(name="buy_seq_no")
    Long buySeqNo  = null ; //'상품 구매 순번',

    @Column(name="price")
    Float price = null ; //'구매 상품 가격',

    @Column(name="goods_price")
    Float goodsPrice = null ; //'구매 상품 가격',

    @Column(name="vat")
    Float vat = 0.0f ; //  '구매 상품 VAT',

    @Column(name="process")
    Integer process = 0; // '0: 결제요청(승인대기), 1:결제승인완료, 2:결제취소, 3:사용완료,4:사용완료 후 환불',

    @Column(name="memo")
    String memo = null ; // '결제 취소등 사유',

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_review_exist")
    Boolean isReviewExist = false ;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="pay_datetime" ,insertable=true, updatable=true)
    String payDatetime  = null ; // '결제승인 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cancel_datetime",insertable=false, updatable=true)
    String cancelDatetime  = null ; // '결제취소 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="use_datetime",insertable=false, updatable=true)
    String useDatetime  = null ; // '사용완료 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="refund_datetime",insertable=false, updatable=true)
    String refundDatetime  = null ; // '사용완료 후 환불 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_datetime", insertable=true, updatable=false)
    String expireDatetime  = null ; // '결제 후 사용처리 기한초과 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="order_datetime", insertable=true, updatable=true)
    String orderDatetime  = null ; // 오더 상품 주문 시각


    @Key
    @Column(name="process_rollback")
    Integer processRollback  = 0 ; // '사용 취소 Cancel 철회',

    @Key
    @Column(name="order_type")
    Integer orderType  = 0 ; // 0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)

    @Key
    @Column(name="order_process")
    Integer orderProcess  = 0 ; // 0:접수대기 1:상품준비중 2:배달/배송중 3:배달/배송완료 4:반품요청 5:반품완료 6:교환요청 7:교환완료 8:구매확정

    @Column(name="point_ratio")
    Float pointRatio  = null ;

    @Column(name="commission_ratio")
    Float commissionRatio  = null ;

    @Column(name="saved_point")
    Integer savedPoint  = null ;

    @Column(name="agent_seq_no")
    Long agentSeqNo  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_payment_point")
    Boolean isPaymentPoint   = false ;

    @Column(name="title")
    String title = null ;

    @Column(name="service_condition")
    String serviceCondition  = null ;

    @Column(name="time_option")
    String timeOption  = null ;

    @Column(name="type")
    Integer type  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="all_days")
    Boolean allDays;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="all_weeks")
    Boolean allWeeks;

    @Column(name="day_of_weeks")
    String dayOfWeeks  = null ;

    @Convert(converter = JpaConverterTime.class)
    @Column(name="start_time")
    String startTime  = null ; //'구매 시작 시각',

    @Convert(converter = JpaConverterTime.class)
    @Column(name="end_time")
    String endTime  = null ; //'구매 종료 시각',

    @Column(name="option_price")
    Integer optionPrice  = null ;

    @Column(name="unit_price")
    Float unitPrice  = null ;

    @Column(name="delivery_fee")
    Integer deliveryFee  = null ;

    @Column(name="transport_number")
    String transportNumber  = null ;

    @Column(name="receiver_name")
    String receiverName  = null ;

    @Column(name="receiver_tel")
    String receiverTel  = null ;

    @Column(name="receiver_post_code")
    String receiverPostCode  = null ;

    @Column(name="receiver_address")
    String receiverAddress  = null ;

    @Column(name="delivery_memo")
    String deliveryMemo  = null ;

    @Column(name="review_point")
    Integer reviewPoint  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="delivery_start_datetime")
    String deliveryStartDatetime  = null ; // '배송 or 배달 시작시간',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="delivery_complete_datetime")
    String deliveryCompleteDatetime  = null ; // '배송 or 배달 완료시간',

    @Column(name="shipping_company")
    String shippingCompany  = null ;

    @Column(name="shipping_company_code")
    String shippingCompanyCode  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="complete_datetime")
    String completeDatetime  = null ; // '구매 완료시간',

    @Column(name="supply_page_seq_no", updatable = false)
    Long supplyPageSeqNo  = null ; // '상점페이지 순번',

    @Column(name="goods_price_seq_no", updatable = false)
    Long goodsPriceSeqNo  = null ; // '상점페이지 순번',

    @Column(name="supply_price")
    private Float supplyPrice;

    @Column(name="supply_price_payment_fee")
    private Float supplyPricePaymentFee;//공급가 결제수수료

    @Column(name="benefit_payment_fee")
    private Float benefitPaymentFee;//수익금 결제수수료

    @Column(name="delivery_fee_payment_fee")
    private Float deliveryFeePaymentFee;//배송비 결제수수료

    @Column(name="supply_price_fee")
    private Float supplyPriceFee;//공급가 수수료

    @Column(name="benefit_fee")
    private Float benefitFee;//수익금 수수료

    @Column(name="delivery_fee_fee")
    private Float deliveryFeeFee;//배송비 수수료

    @Column(name="return_payment_price")
    private Float returnPaymentPrice;//핀테크 반환금액

    @Column(name="payment_fee")
    private Float paymentFee;//결제수수료

    @Column(name="platform_fee")
    private Float platformFee;//플렛폼수수료

    @Transient
    private List<BuyGoodsOption> buyGoodsOptionSelectList = new ArrayList<BuyGoodsOption>();

    public Float calculateVat(){
        return (this.price * this.count)/11.0f ;
    }
}
