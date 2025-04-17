package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolPurchase")
@Table(name = "lucky_bol_purchase")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolPurchase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "lucky_bol_seq_no")
    private Long luckyBolSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name="title")
    private String title = null ;

    @Column(name = "win_number")
    private String winNumber;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable=false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    private String modDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_datetime")
    private String expireDatetime;

    @Column(name = "engaged_price")
    private Integer engagedPrice;

    @Column(name = "engaged_count")
    private Integer engagedCount;

    private String status;

    @Column(name = "purchase_seq_no")
    private Long purchaseSeqNo;

    @Column(name = "engage_type")
    private String engageType;

    @Column(name="order_id")
    private String orderId = null ;  //'결제 주문 아이디'

    @Column(name="pay_method")
    private String payMethod;  // card, point, easy

    @Column(name="pg")
    private String pg;

    @Column(name="pg_tran_id")
    private String pgTranId = null ; // 'PG사 결제번호 아이디',

    @Column(name="buyer_email")
    private String buyerEmail = null ;

    @Column(name="buyer_name")
    private String buyerName = null ;

    @Column(name="buyer_tel")
    private String buyerTel = null ;

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

    @Column(name="receiver_name")
    private String receiverName;

    @Column(name="receiver_tel")
    private String receiverTel;

    @Column(name="receiver_post_code")
    private String receiverPostCode;

    @Column(name="receiver_address")
    private String receiverAddress;

    @Column(name="receiver_address_detail")
    private String receiverAddressDetail;

    @Column(name="delivery_memo")
    private String deliveryMemo;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "lucky_bol_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private LuckyBol luckyBol;

}
