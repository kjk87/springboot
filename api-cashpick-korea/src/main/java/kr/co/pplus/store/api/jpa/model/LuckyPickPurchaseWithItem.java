package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity(name = "luckyPickPurchaseWithItem")
@Table(name = "lucky_pick_purchase")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyPickPurchaseWithItem implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="lucky_pick_seq_no")
    private Long luckyPickSeqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;

    @Column(name="order_no")
    private String orderNo;

    @Column(name="sales_type")
    private String salesType; // delivery
    private String title; // luckypick title

    @Column(name = "payment_method")
    private String paymentMethod; // card, point
    private Integer quantity; // 구매수량
    private Float price; // 결제금액
    @Column(name="unit_price")
    private Float unitPrice; // 개당가격
    @Column(name="cancel_quantity")
    private Integer cancelQuantity; // 취소수량 합
    @Column(name="cancel_price")
    private Float cancelPrice; // 취소금액 합
    @Column(name="remain_price")
    private Float remainPrice; // 취소후 남은 금액

    private Integer status; //1:결제요청, 2:결제승인, 3:취소, 4:부분취소

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="payment_datetime")
    private String paymentDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="change_status_datetime")
    private String changeStatusDatetime;

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

    @Column(name="is_cancelable")
    private Boolean isCancelable;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="lucky_pick_purchase_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @Where(clause = "status = 2 and is_open = false")
    private Set<LuckyPickPurchaseItemOnly> itemList;
}
