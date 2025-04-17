package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;

@Entity(name="buyGoodsWithDate") // This tells Hibernate to make a table out of this class
@Table(name="buy_goods")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyGoodsWithDate {

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
    HashMap<String, Object> goodsProp  = null ; // '구매 상품 옵션',


    @Column(name="reg_datetime", updatable = false)
    Date regDatetime  = null ; // '장바구니 상품 등록 시각',

    @Column(name="mod_datetime")
    Date modDatetime  = null ; // '장바구니 상품 변경 시각',


    @Key
    @Column(name="buy_seq_no")
    Long buySeqNo  = null ; //'상품 구매 순번',

    @Column(name="price")
    Float price = null ; //'구매 상품 가격',

    @Column(name="vat")
    Float vat = 0.0f ; //  '구매 상품 VAT',

    @Column(name="process")
    Integer process = 0; // '0: 결제요청(승인대기), 1:결제승인완료, 2:결제취소, 3:사용완료,4:사용완료 후 환불',

    @Column(name="memo")
    String memo = null ; // '결제 취소등 사유',

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_review_exist")
    Boolean isReviewExist = false ;


    @Column(name="pay_datetime" ,insertable=false, updatable=true)
    Date payDatetime  = null ; // '결제승인 시각',


    @Column(name="cancel_datetime",insertable=false, updatable=true)
    Date cancelDatetime  = null ; // '결제취소 시각',

    @Column(name="use_datetime",insertable=false, updatable=true)
    Date useDatetime  = null ; // '사용완료 시각',

    @Column(name="refund_datetime",insertable=false, updatable=true)
    Date refundDatetime  = null ; // '사용완료 후 환불 시각',

    @Column(name="expire_datetime")
    Date expireDatetime  = null ; // '결제 후 사용처리 기한초과 시각',

    @Column(name="order_datetime", insertable=true, updatable=true)
    Date orderDatetime  = null ; // 오더 상품 주문 시각

    @Key
    @Column(name="process_rollback")
    Integer processRollback  = 0 ; // '사용 취소 Cancel 철회',

    @Key
    @Column(name="order_type")
    Integer orderType  = 0 ; // 0: 매장주문, 1포장주문, 2:배달주문, 3:배송(reserved)

    @Key
    @Column(name="order_process")
    Integer orderProcess  = 0 ; // 0: 접수대기, 1:접수완료, 2:완료, 3: 주문최소, 4:배송중(reserved)' ;

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

}
