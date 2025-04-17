package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity(name = "luckyBoxDeliveryPurchase")
@Table(name = "luckybox_delivery_purchase")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxDeliveryPurchase implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="luckybox_purchase_item_seq_no")
    private Long luckyBoxPurchaseItemSeqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "payment_method")
    private String paymentMethod; // card, point
    private Float price; // 결제금액

    @Column(name = "pg_price")
    private Integer pgPrice; // pg금액

    @Column(name = "use_point")
    private Integer usePoint; // 사용포인트

    private Integer status; //1:결제요청, 2:결제승인, 3:취소

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="payment_datetime")
    private String paymentDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cancel_datetime")
    private String cancelDatetime;


    // 결제요청 응답 파라미터
    @Column(name="pay_response_approval_no")
    private String payResponseApprovalNo; // 승인번호
    @Column(name="pay_response_card_id")
    private String payResponseCardId; // 카드 아이디
    @Column(name="pay_response_card_nm")
    private String payResponseCardNm; // 카드명
    @Column(name="pay_response_card_no")
    private String payResponseCardNo; // 카드명
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

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "luckybox_purchase_item_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    LuckyBoxPurchaseItemOnly luckyBoxPurchaseItem = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @Transient
    LuckyBoxPurchaseItemOption selectOption;

    @Transient
    LuckyBoxDelivery selectDelivery;
}
