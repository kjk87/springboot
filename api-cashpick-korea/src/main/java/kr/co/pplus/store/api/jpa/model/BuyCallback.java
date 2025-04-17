package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterEncryptJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.util.RedisUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity(name="buyCallback") // This tells Hibernate to make a table out of this class
@Table(name="buy_callback")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyCallback {


    public BuyCallback(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 구매 Callback 순번',



    @Key
    @Column(name="buy_seq_no")
    Long buySeqNo = null ;  //'상품 구매 순번',

    @Key
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;  //'상품 구매 사용자 순번',

    @Key
    @Column(name="order_id")
    String orderId = null ;  //'결제 주문 아이디',

    @Key
    @Column(name="pg_tran_id")
    String pgTranId = null ;  //'결제 주문 승인 아이디' bootpay.receipt_id,

    @Key
    @Column(name="application_id")
    String applicationId = null  ;  //'bootPay 앱 ID',

    @Key
    @Column(name="private_key")
    String privateKey = null ;  //'bootPay 개인 Key ',

    @Key
    @Column(name="status")
    String status ; // PG 승인 상태 값 -  0:미승인, 1:승인

    @Column(name="price")
    Integer price ; // PG 결제 값

    @Column(name="pg")
    String pg ; // PG

    @Column(name="pg_name")
    String pgName ; // PG 명

    @Column(name="method")
    String method ;

    @Column(name="method_name")
    String methodName ;

    @Column(name="name")
    String name ; // PG

    @Lob
    @Convert(converter = JpaConverterEncryptJson.class)
    @Column(name="payment_data")
    Map<String, String> paymentData ;


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;  //'등록 시각',

    @Column(name="retry_count")
    Integer retryCount = null ;
}
