package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterEncryptJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Map;

@Entity(name="lpngCallback") // This tells Hibernate to make a table out of this class
@Table(name="lpng_callback")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LpngCallback {


    public LpngCallback(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 구매 Callback 순번',


    @Key
    @Column(name="buy_seq_no")
    Long buySeqNo = null ;  //'상품 구매 순번',

    // @Key
    @Column(name="purchase_seq_no")
    Long purchaseSeqNo = null ;

    @Column(name="point_buy_seq_no")
    Long pointBuySeqNo = null ;

    @Column(name="event_buy_seq_no")
    Long eventBuySeqNo = null ;

    @Column(name="order_purchase_seq_no")
    Long orderPurchaseSeqNo = null ;

    @Key
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Key
    @Column(name="order_id")
    String orderId = null ;  //'결제 주문 아이디',

    @Key
    @Column(name="pg_tran_id")
    String pgTranId = null ;  //'결제 주문 승인 아이디'

    @Key
    @Column(name="status")
    Boolean status ; // PG 승인 상태 값 -  true:승인, false:승인거절 또는 오류


    @Column(name="name")
    String name ; // 결제자 명

    @Column(name="price")
    Integer price ; // 결제금액

    @Column(name="appr_date")
    String apprDate ; // 결제일

    @Column(name="appr_time")
    String apprTime ;  // 결제 시각

    @Lob
    @Convert(converter = JpaConverterEncryptJson.class)
    @Column(name="payment_data")
    Map<String, String> paymentData ;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;  //'등록 시각',

    @Column(name="lpng_order_no")
    String lpngOrderNo ;  // 결제 시각

    @Column(name="process")
    Integer process ;  // 0:결제대기, 1:결제완료, 2: 취소

    @Key
    @Column(name="result_seq_no")
    Long resultSeqNo = null ;

    @Column(name="memo")
    String memo ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="result_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false, nullable = true)
    private LpngCallbackResult result;
}
