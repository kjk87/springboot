package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.util.RedisUtil;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name="purchase")
@Table(name="purchase")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Purchase {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ; // '상품 구매 순번',

    private String code;

    @Column(name="member_seq_no")
    private Long memberSeqNo = null ;  //'상품 구매 회원

    @Column(name="page_seq_no")
    private  Long pageSeqNo = null ;  //'상점 순번',

    @Column(name="order_id")
    private String orderId = null ;  //'결제 주문 아이디',
    
    @Column(name="sales_type")
    private Long salesType; // 판매분류 - 1:매장판매, 2:배달, 3:배송, 4:예약, 5:픽업
    
    /*
      1:결제요청, 2:결제승인
      11:취소요청, 12:부분취소요청, 13:취소완료, 14:부분취소완료
      21:환불요청, 22:부분환불요청, 23:환불완료, 24:부분환불완료 
      31:교환요청, 32:부분교환요청, 33:교환완료, 34:부분교환완료
      99:완료처리
     */
    private Integer status; 
    
    @Column(name="pay_method")
    private String payMethod;  // card, point, easy

    @Column(name="app_type")
    private String appType; // luckyball, nonMember, pplus

    @Column(name="pg")
    private String pg;

    @Column(name="pg_tran_id")
    private String pgTranId = null ; // 'PG사 결제번호 아이디',


    @Column(name="title")
    private String title = null ;
    

    @Column(name="buyer_email")
    private String buyerEmail = null ;

    @Column(name="buyer_name")
    private String buyerName = null ;

    @Column(name="buyer_tel")
    private String buyerTel = null ;
    

    private Float price; // 결제금액

    @Column(name = "pg_price")
    private Integer pgPrice; // pg금액

    @Column(name = "use_cash")
    private Integer useCash; // 사용캐시

    @Column(name = "use_point")
    private Integer usePoint; // 사용포인트

    @Column(name = "coupon_seq_no")
    private Long couponSeqNo;

    @Column(name = "member_lucky_coupon_seq_no")
    private Long memberLuckyCouponSeqNo;

    @Column(name = "coupon_price")
    private Integer couponPrice;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name="product_price")
    private Float productPrice; // 상품가격 합.
    @Column(name="option_price")
    private Float optionPrice; // 옵션가격 합계
    
    @Column(name="delivery_fee")
    private Float deliveryFee; // 배송비합(추가배송비 제외)
    @Column(name="delivery_add_fee")
    private Float deliveryAddFee; // 제주/도서산간 추가배송비 합
    
    @Column(name="non_member")
    private Boolean nonMember; // 비회원구매
    
    @Column(name="finteck_result")
    private String finteckResult; 
    
    @Column(name="return_payment_price")
    private Float returnPaymentPrice; // 

    @Column(name="saved_point")
    private Float savedPoint;
    
    @Column(name="agent_seq_no")
    private Long agentSeqNo;
    
    @Column(name="login_id")
    private String loginId;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    private String modDatetime;

    @Column(name="appr_no")
    private String apprNo;

    @Column(name="receipt_id")
    private String receiptId;

    @Column(name="pay_type")
    private String payType; //fintech, reappay

    // 결제요청 응답 파라미터
    @Column(name="pay_response_approval_no")
    private String payResponseApprovalNo; // 승인번호
    @Column(name="pay_response_card_id")
    private String payResponseCardId; // 카드 아이디
    @Column(name="pay_response_card_nm")
    private String payResponseCardNm; // 카드명
    @Column(name="pay_response_card_no")
    private String payResponseCardNo; // 카드번호
    @Column(name="pay_response_cert_yn")
    private Boolean payResponseCertYn; // 인증여부
    @Column(name="pay_response_code")
    private String payResponseCode; //pay 결과코드
    @Column(name="pay_response_installment")
    private String payResponseInstallment; // 할부
    @Column(name="pay_response_msg")
    private String payResponseMsg; // pay 결과 메세지
    @Column(name="pay_response_order_no")
    private String payResponseOrderNo; // 주문번호
    @Column(name="pay_response_pay_date")
    private String payResponsePayDate; // pay 날짜
    @Column(name="pay_response_pay_time")
    private String payResponsePayTime; // pay 시간
    @Column(name="pay_response_pay_type")
    private String payResponsePayType; // 지불수단
    @Column(name="pay_response_pg_seq")
    private String payResponsePgSeq; // pg사 seq Key
    @Column(name="pay_response_product_type")
    private String payResponseProductType; //
    @Column(name="pay_response_sell_mm")
    private String payResponseSellMm; // 할부개월
    @Column(name="pay_response_test_yn")
    private Boolean payResponseTestYn; // 테스트 여부
    @Column(name="pay_response_tran_seq")
    private String payResponseTranSeq; // 거래번호
    @Column(name="pay_response_zerofee_yn")
    private Boolean payResponseZerofeeYn; // 무이자 여부

    @Column(name="purchase_type")
    private String purchaseType; //store, luckyBol, firstServed

    @Column(name="lucky_bol_purchase_seq_no")
    private Long luckyBolPurchaseSeqNo;

    @Column(name="lucky_bol_seq_no")
    private Long luckyBolSeqNo;

    @Column(name="first_served_seq_no")
    private Long firstServedSeqNo;

    @Transient
    private List<PurchaseProduct> purchaseProductSelectList = null;


    public boolean isValidOrderId(final String prefix) {
        if( this.orderId == null ){
            return false ;
        }
        else {
            //orderId 검증 코드
            String key = prefix + orderId;
            String value = RedisUtil.getInstance().getOpsHash(key, "orderId");
            if( value != null && orderId.equals(value) )
                return true ;
            else
                return false ;
        }
    }
    
}
