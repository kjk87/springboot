package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity(name="orderPurchaseDetail")
@Table(name="order_purchase")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPurchaseDetail {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;

    private String code;

    @Column(name="member_seq_no", updatable = false)
    private Long memberSeqNo;

    @Column(name="page_seq_no", updatable = false)
    private Long pageSeqNo;

    @Column(name="agent_seq_no")
    private Long agentSeqNo;

    @Column(name="wholesale_code")
    private String wholesaleCode;
    @Column(name="distributor_code")
    private String distributorCode;
    @Column(name="dealer_code")
    private String dealerCode;

    @Column(name="page_commission_ratio")
    private Float pageCommissionRatio;
    @Column(name="wholesale_commission_ratio")
    private Float wholesaleCommissionRatio;
    @Column(name="distributor_commission_ratio")
    private Float distributorCommissionRatio;
    @Column(name="dealer_commission_ratio")
    private Float dealerCommissionRatio;

    @Column(name="page_commission")
    private Float pageCommission;
    @Column(name="wholesale_commission")
    private Float wholesaleCommission;
    @Column(name="distributor_commission")
    private Float distributorCommission;
    @Column(name="dealer_commission")
    private Float dealerCommission;

    @Column(name="order_id")
    private String orderId;


    @Column(name="sales_type")
    private Long salesType; // 판매분류 - 1:매장판매, 2:배달, 3:배송, 4:예약, 5:포장, 6:티켓

    private Integer status; // 1:결제요청, 2:결제승인 11:취소요청, 13:취소완료, 99:완료처리

    @Column(name="status_rider")
    private Integer statusRider; // 배달 0:접수대기, 1:접수완료, 2:배달취소, 3:기사배정  4:배달중(기사픽업), 99:배달완료

    @Column(name="status_shop")
    private Integer statusShop; // 매장 0:접수대기, 1:접수완료, 2:취소, 99:사용완료

    @Column(name="status_pack")
    private Integer statusPack; // 포장 0:접수대기, 1:접수완료, 2:포장취소, 99:포장완료

    @Column(name="status_reserve")
    private Integer statusReserve; // 예약 0:접수대기, 1:접수완료, 2:예약취소, 99:사용완료

    @Column(name="status_ticket")
    private Integer statusTicket; // 티켓 0:접수대기, 1:접수완료, 2:취소, 3:기간만료, 4:사용요청, 99:사용완료

    @Column(name="is_status_completed")
    private Boolean isStatusCompleted; // 모든 상태 완료된 값.(구매확정, 배달완료, 사용완료 값이 되면 상태값 true)

    private String title;

    @Column(name="app_type")
    private String appType; // luckyball, nonMember, prnumber,

    @Column(name="pay_method")
    private String payMethod;  // card, point, outsideCard, outsideCash

    @Column(name="amount")
    private Integer amount; // 주문메뉴갯수-옵션제외

    @Column(name="price")
    private Float price; // 결제금액

    @Column(name="option_price")
    private Float optionPrice; // 옵션가격의 합계 optionPrice * count

    @Column(name="menu_price")
    private Float menuPrice; // 결제금액 - 배달비

    @Column(name="rider_fee")
    private Float riderFee; // 배달비


    @Column(name="saved_point")
    private Float savedPoint;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "visit_time")
    private String visitTime; // 방문시간

    @Column(name="rider_payment_type")
    private Integer riderPaymentType; // 1:무료, 2:유료, 3:조건부무료
    @Column(name="rider_time")
    private Integer riderTime;
    @Column(name="rider_company")
    private String riderCompany;
    @Column(name="rider_company_code")
    private String riderCompanyCode;
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "rider_start_time")
    private String riderStartTime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "rider_complete_time")
    private String riderCompleteTime;



    @Column(name = "finteck_result")
    private String finteckResult;

    @Column(name="return_payment_price")
    private Float returnPaymentPrice; // 핀테크 반환금액

    @Column(name="payment_fee")
    private Float paymentFee; // 결제수수료

    @Column(name="platform_fee")
    private Float platformFee; // 플랫폼 수수료

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "pay_datetime")
    private String payDatetime; // 결제승인 시각


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "cancel_datetime")
    private String cancelDatetime; // 취소요청시간

    @Column(name="cancel_memo")
    private String cancelMemo; //취소 메모


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="change_status_datetime")
    private String changeStatusDatetime;

    @Column(name="recommended_member_seq_no")
    private Long recommendedMemberSeqNo;

    @Column(name="recommended_member_type")
    private String recommendedMemberType;

    @Column(name="recommended_member_point")
    private Float recommendedMemberPoint;


    @Column(name="pg")
    private String pg;

    @Column(name="pg_tran_id")
    private String pgTranId;

    @Column(name = "appr_no")
    private String apprNo;

    @Column(name = "appr_tran_no")
    private String apprTranNo;

    @Column(name = "receipt_id")
    private String receiptId;

    private String memo;


    @Column(name = "disposable_required")
    private Boolean disposableRequired;

    private String phone;
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "visit_number")
    private Integer visitNumber;

    @Formula("(select count(1) from order_menu_review omr where omr.order_purchase_seq_no = seq_no) > 0")
    private Boolean isReviewExist = false;

    @Column(name = "is_visit_now")
    private Boolean isVisitNow;

    @Column(name = "pick_min")
    private Integer pickMin;

    @Column(name = "delivery_id")
    private String deliveryId;

    @Column(name = "page_rider_fee")
    private Float pageRiderFee;

    @Column(name = "expected_pickup_time")
    private String expectedPickupTime;

    @Column(name = "add_price")
    private Float addPrice;

    @Column(name = "add_rider_fee")
    private Float addRiderFee;

    @Column(name = "rider_distance")
    private Float riderDistance;

    @Column(name = "rider_name")
    private String riderName;

    @Column(name = "rider_tel")
    private String riderTel;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "receipt_datetime")
    private String receiptDatetime;

    @Column(name = "rider_type")
    private Integer riderType;

    @Column(name = "expire_datetime")
    private Date expireDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "used_datetime")
    private String usedDatetime;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_purchase_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @OrderBy("seq_no ASC")
    private Set<OrderPurchaseMenuDetail> orderPurchaseMenuList ;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Member member;
}
