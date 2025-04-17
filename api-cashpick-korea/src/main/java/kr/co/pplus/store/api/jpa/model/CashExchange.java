package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Map;

@Entity(name="cashExchange") // This tells Hibernate to make a table out of this class
@Table(name="cash_exchange")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CashExchange {

    public CashExchange(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // 환전 순번,

    @Key
    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo  = null ; // '사용자 순번',

    Long bol  = null ; // '환전 볼',

    Long point  = null ; // '환전 포인트',

    @Column(name="cash")
    Long cash  = null ; // '환전 현금',

    @Column(name="refund_cash")
    Long refundCash  = null ; // '실제 환전 현금(제세 공과금 22% 제외)',

    @Column(name="status")
    Integer status  = null ;

    @Column(name="bank_name")
    String bankName  = null ; //'입금기관명',

    @Column(name="bank_account_id")
    String bankAccountId  = null ; //'입금계좌번호',

    @Column(name="bank_account_holder_name")
    String bankAccountHolderName  = null ; //'수취인 성명',

    String reason  = null ;

    @Column(name="member_type")
    String memberType  = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; // '장바구니 상품 등록 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; // '장바구니 상품 변경 시각',

    @Transient
    private Long cashExchangeRateSeqNo = null;

}
