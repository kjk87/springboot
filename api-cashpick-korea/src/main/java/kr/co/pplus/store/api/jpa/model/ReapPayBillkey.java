package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity(name="reapPayBillkey")
@Table(name="reappay_billkey")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReapPayBillkey {

    public ReapPayBillkey(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="billkey_issuer_card_type")
    String billkeyIssuerCardType = null ;

    @Column(name="billkey_issuer_card_name")
    String billkeyIssuerCardName = null ;

    @Column(name="billkey_masked_card_numb")
    String billkeyMaskedCardNumb = null ;

    @Column(name="billkey_billing_token")
    String billkeyBillingToken = null ;

    @Column(name="billkey_card_type")
    String billkeyCardType = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;

    Boolean represent;


}
