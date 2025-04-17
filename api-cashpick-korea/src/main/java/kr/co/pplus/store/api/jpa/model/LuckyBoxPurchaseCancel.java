package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "luckyBoxPurchaseCancel")
@Table(name = "luckybox_purchase_cancel")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxPurchaseCancel implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="luckybox_purchase_seq_no")
    private Long luckyBoxPurchaseSeqNo;
    @Column(name="luckybox_purchase_item_seq_no")
    private Long luckyBoxPurchaseItemSeqNo;

    // 취소요청 응답 파라미터
    @Column(name="pay_response_amt")
    private String payResponseAmt; // 취소금액

    @Column(name="pay_response_approval_no")
    private String payResponseApprovalNo; // 승인번호
    @Column(name="pay_response_approval_ymdhms")
    private String payResponseApprovalYMDHMS; // 승일일시
    @Column(name="pay_response_card_id")
    private String payResponseCardId; // 카드 아이디
    @Column(name="pay_response_card_nm")
    private String payResponseCardNm; // 카드명
    @Column(name="pay_response_cert_yn")
    private String payResponseCertYn; // 인증여부
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
//    @Column(name="pay_response_pg_seq")
//    private String payResponsePgSeq; // pg사 seq Key
//    @Column(name="pay_response_product_type")
//    private String payResponseProductType; //
    @Column(name="pay_response_sell_mm")
    private String payResponseSellMm; // 할부개월
    @Column(name="pay_response_test_yn")
    private Boolean payResponseTestYn; // 테스트 여부
    @Column(name="pay_response_tran_seq")
    private String payResponseTranSeq; // 거래번호
    @Column(name="pay_response_zerofee_yn")
    private Boolean payResponseZerofeeYn; // 무이자 여부
    @Column(name="pay_response_part_cancel_flag")
    private String payResponsePartCancelFlag; // 부분취소여부
    @Column(name="pay_response_remain_amt")
    private Float payResponseRemainAmt; // 남은금액

}
