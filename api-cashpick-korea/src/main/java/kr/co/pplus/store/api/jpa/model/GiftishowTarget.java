package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterMobileNumber;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="giftishowTarget")
@Table(name="giftishow_target")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftishowTarget {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;

    @Column(name="giftishow_buy_seq_no")
    private Long giftishowBuySeqNo = null ;

    @Convert(converter = JpaConverterMobileNumber.class)
    @Column(name="mobile_number")
    String mobileNumber = null ; //'휴대폰 번호',

    String name = null ;

    @Column(name="tr_id")
    String trId = null ;

    @Column(name="order_no")
    String orderNo = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime = null ;

}
