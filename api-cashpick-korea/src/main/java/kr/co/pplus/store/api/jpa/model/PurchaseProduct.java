package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity(name="purchaseProduct")
@Table(name="purchase_product")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseProduct {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ;

    private String code;

	@Key
    @Column(name="purchase_seq_no", updatable = false)
	private Long purchaseSeqNo;
	
	@Column(name="sales_type")
    private Long salesType; // 판매분류 - 1:매장판매, 2:배달, 3:배송, 4:예약, 5:픽업

    @Key
    @Column(name="member_seq_no", updatable = false)
    private Long memberSeqNo; // 구매회원
    
    @Key
    @Column(name="page_seq_no", updatable = false)
    private Long pageSeqNo;
    
    @Key
    @Column(name="friend_member_seq_no", updatable = false)
    private Long friendMemberSeqNo; // 가맹점이 아닌 친구 판매회원 정보(이후에 적용예정)

    @Key
    @Column(name="product_seq_no", updatable = false)
    private Long productSeqNo;
    
    @Key
    @Column(name="product_price_code", updatable = false)
    private String productPriceCode;

    @Key
    @Column(name="purchase_delivery_seq_no", updatable = false)
    private Long purchaseDeliverySeqNo;

    @Key
    @Column(name="product_delivery_seq_no", updatable = false)
    private Long productDeliverySeqNo;
    
    private Integer status; // 결제 상태값. 1:결제요청, 2:결제승인, 11:취소요청, 12:취소완료, 21:환불요청, 22:환불완료, 31:교환요청, 32:교환완료
    @Column(name="delivery_status")
    private Integer deliveryStatus; // 배송 상태값. 1:상품준비중, 2:주문취소, 3:배송중, 4:배송완료, 5:환불수거중, 6:환불수거완료, 7:교환수거중, 8:교환상품준비중, 9:교환배송중, 10:교환배송완료, 99:구매확정 
    @Column(name="rider_status")
    private Integer riderStatus; // 배달 상태값. 1:배달준비, 2:배달취소, 3:배달중, 99:배달완료 
    @Column(name="reserve_status")
    private Integer reserveStatus; // 예약 상태값. 1:예약중, 2:예약취소, 99:사용완료
    
    @Column(name="is_status_completed")
    private Boolean isStatusCompleted; // 모든 상태 완료된 값.(구매확정, 배달완료, 사용완료 값이 되면 상태값 true)

    private String title; // 상품명 

    
    @Column(name="count")
    private Integer count;

    @Column(name="price")
    private Float price; // 구매 상품 가격
    
    @Column(name="product_price")
    private Float productPrice; // 상품가격+옵션가격 합계(배송비 제외)

    @Column(name="unit_price")
    private Float unitPrice; //상품 개당가격 
    
    @Column(name="option_price")
    private Integer optionPrice; // 옵션가격의 합계 optionPrice * count
    
    
    
    @Column(name="supply_page_seq_no")
    private Long supplyPageSeqNo; // 도매 가맹점
    
    
    @Column(name="supply_price")
    private Float supplyPrice; // supplyPrice * count
    
    @Column(name="supply_price_payment_fee")
    private Float supplyPricePaymentFee; // 공급가 결제수수료
    
    @Column(name="benefit_payment_fee")
    private Float benefitPaymentFee; // 수익금 결제수수료
    
    @Column(name="delivery_fee_payment_fee")
    private Float deliveryFeePaymentFee; // 배송비 결제수수료
    
    @Column(name="supply_price_fee")
    private Float supplyPriceFee; // 공급가 수수료
    
    @Column(name="benefit_fee")
    private Float benefitFee; // 수익금 수수료
    
    @Column(name="delivery_fee_fee")
    private Float deliveryFeeFee; // 배송비 수수료
    
    @Column(name="return_payment_price")
    private Float returnPaymentPrice; // 핀테크 반환금액
    
    @Column(name="payment_fee")
    private Float paymentFee; // 결제수수료 
    
    @Column(name="platform_fee")
    private Float platformFee; // 플랫폼 수수료 

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="pay_datetime" ,insertable=true, updatable=true)
    private String payDatetime; // 결제승인 시각


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cancel_datetime")
    private String cancelDatetime; // 취소요청시간
    
    @Column(name="cancel_memo")
    private String cancelMemo; //취소 메모

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="exchange_datetime",insertable=false, updatable=true)
    private String exchangeDatetime; // 교환요청시간
    
    @Column(name="exchange_memo")
    private String exchangeMemo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="refund_datetime",insertable=false, updatable=true)
    private String refundDatetime; // 환불요청시간
    
    @Column(name="refund_memo")
    private String refundMemo; //취소 메모


    @Column(name="agent_seq_no")
    private Long agentSeqNo;
    
    @Column(name="is_payment_point")
    private Boolean isPaymentPoint;
    
    @Column(name="saved_point")
    private Float savedPoint;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="change_status_datetime")
    private String changeStatusDatetime;

    @Column(name="end_date")
    private Date endDate;

    @Column(name="recommended_member_seq_no")
    private Long recommendedMemberSeqNo;

    @Column(name="recommended_member_type")
    private String recommendedMemberType;

    @Column(name="ticket_product_type")
    private String ticketProductType;

    @Convert(converter = JpaConverterTime.class)
    @Column(name="start_time")
    String startTime  = null ;

    @Convert(converter = JpaConverterTime.class)
    @Column(name="end_time")
    String endTime  = null ;

    @Column(name = "sub_title")
    String subTitle;

    @Column(name = "domeme_order_no")
    Long domemeOrderNo;

    @Column(name="is_payment_bol")
    private Boolean isPaymentBol;

    @Column(name="saved_bol")
    private Integer savedBol;

    @Column(name="lucky_bol_purchase_seq_no")
    private Long luckyBolPurchaseSeqNo;

    @Column(name="lucky_bol_seq_no")
    private Long luckyBolSeqNo;

    @Column(name="lucky_bol_select_type")
    private String luckyBolSelectType;

    @Column(name="first_served_seq_no")
    private Long firstServedSeqNo;

    @Transient
    private List<PurchaseProductOption> purchaseProductOptionSelectList = null;

    @Transient
    private PurchaseDelivery purchaseDeliverySelect = null;

    @Transient
    ProductPrice productPriceData;
    
}
