package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterEncryptJson;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Map;

@Entity(name="lpngCallbackResult") // This tells Hibernate to make a table out of this class
@Table(name="lpng_callback_result")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LpngCallbackResult {


    public LpngCallbackResult(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="return_code")
    private String returnCode;
    @Column(name="error_msg")
    private String errorMsg;

    @Column(name="shop_code")
    private String shopCode ;
    @Column(name="order_no")
    private String orderNo ;
    @Column(name="order_status")
    private String orderStatus ;
    @Column(name="order_expiredt")
    private String orderexpiredt ;

    @Column(name="result_code")
    private String resultCode;

    @Column(name="error_code")
    private String errorCode ;

    @Column(name="comp_order_no")
    private String compOrderNo ;   // 업체주문번호
    @Column(name="comp_mem_no")
    private String compMemNo ;   // 업체멤버아이디 or 멤버번호
    @Column(name="order_goodsname")
    private String orderGoodsname ;  // 주문 제품명
    @Column(name="order_req_amt")
    private String orderReqAmt ;  // 요청금액
    @Column(name="order_name")
    private String orderName ;   // 주문자명
    @Column(name="order_hp")
    private String orderHp ;  // 주문자 휴대폰
    @Column(name="order_email")
    private String orderEmail ; // 주문자 이메일
    @Column(name="comp_temp1")
    private String compTemp1 ;
    @Column(name="comp_temp2")
    private String compTemp2 ;
    @Column(name="comp_temp3")
    private String compTemp3 ;
    @Column(name="comp_temp4")
    private String compTemp4 ;
    @Column(name="comp_temp5")
    private String compTemp5 ;

    @Column(name="req_card_no")
    private String reqCardNo ;  // 카드번호

    @Column(name="req_card_month")
    private String reqCardMonth ;  // (*) 카드유효기간 월( MM )
    @Column(name="req_card_year")
    private String reqCardYear ; // (*) 카드유효기간 년( YYYY )
    @Column(name="req_installment")
    private String reqInstallment ; // 할부개월수

    @Column(name="appr_no")
    private String apprNo ;  // 승인번호
    @Column(name="appr_tran_no")
    private String apprTranNo ;  // PG 거래번호

    @Column(name="appr_shop_code")
    private String apprShopCode ; // 상점코드
    @Column(name="appr_date")
    private String apprDate ; // 결제일자
    @Column(name="appr_time")
    private String apprTime ; // 결제시간

    @Column(name="cardtxt")
    private String cardtxt ;  // 매입사명
}
