package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Data
@Entity(name = "luckyBoxPurchaseItem")
@Table(name = "luckybox_purchase_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxPurchaseItem implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="luckybox_seq_no")
    private Long luckyBoxSeqNo;
    @Column(name="luckybox_purchase_seq_no")
    private Long luckyBoxPurchaseSeqNo;
    @Column(name="luckybox_pay_response_tran_seq")
    private String luckyBoxPayResponseTranSeq; // 럭키박스 구매시 거래번호:취소요청시 사용값
    @Column(name="member_seq_no")
    private Long memberSeqNo;

    @Column(name="temp_member")
    private Boolean tempMember;

    @Column(name="luckybox_title")
    private String luckyBoxTitle;

    @Column(name = "payment_method")
    private String paymentMethod; // card, point
    private Float price;

    private Integer status; //1:결제요청, 2:결제승인, 3:취소, 4:캐시백교환
    @Column(name="is_open")
    private Boolean isOpen; // 박스오픈 여부
    @Column(name="delivery_status")
    private Integer deliveryStatus; // 1:배송대기(배송신청), 2:배송중, 3:배송완료

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="payment_datetime")
    private String paymentDatetime;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="open_datetime")
    private String openDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="cancel_datetime")
    private String cancelDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="complete_datetime")
    private String completeDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="exchange_datetime")
    private String exchangeDatetime;

    // 당첨 product 정보
    @Column(name="product_seq_no")
    private Long productSeqNo;
    @Column(name="product_price_seq_no")
    private Long productPriceSeqNo;
    @Column(name="product_delivery_seq_no")
    private Long productDeliverySeqNo;
    @Column(name="supply_page_seq_no")
    private Long supplyPageSeqNo; // 도매 가맹점

    @Column(name="product_type")
    private String productType; // delivery
    @Column(name="product_name")
    private String productName;
    @Column(name="product_image")
    private String productImage;
    @Column(name="product_price")
    private Float productPrice;
    @Column(name="option_name")
    private String optionName;
    @Column(name="option_price")
    private Float optionPrice;
    @Column(name="supply_price")
    private Float supplyPrice; // 공급가
    @Column(name="supply_price_payment_fee")
    private Float supplyPricePaymentFee; // 공급가 결제수수료
    @Column(name="delivery_fee")
    private Float deliveryFee;

    @Column(name="delivery_pay_status")
    private Integer deliveryPayStatus; // 1:결제요청, 2:결제승인, 3:취소,
    @Column(name="luckybox_delivery_purchase_seq_no")
    private Long luckyBoxDeliveryPurchaseSeqNo;
    @Column(name="delivery_payment_price")
    private Float deliveryPaymentPrice; // deliveryFee + optionPrice

    @Column(name="luckybox_delivery_seq_no")
    private Long luckyboxDeliverySeqNo;

    private String impression;

    @Column(name = "refund_bol")
    private Integer refundBol;

    @Formula("(select count(1) from luckybox_review lrv where lrv.luckybox_purchase_item_seq_no = seq_no) > 0")
    private Boolean isReviewExist = false;

    @Formula("(select count(1) from luckybox_reply lr where lr.luckybox_purchase_item_seq_no = seq_no and lr.status = 1)")
    private Integer replyCount;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_price_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    ProductPriceRef productPriceData = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "luckybox_delivery_purchase_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    LuckyBoxDeliveryPurchaseRef luckyBoxDeliveryPurchase = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "luckybox_delivery_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    LuckyBoxDelivery luckyBoxDelivery = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "luckybox_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    LuckyBox luckyBox = null ;

}
