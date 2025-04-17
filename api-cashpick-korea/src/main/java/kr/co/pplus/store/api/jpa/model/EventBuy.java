package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "eventBuy")
@Table(name = "event_buy")
public class EventBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    private Integer status;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "pg_price")
    private Integer pgPrice;

    @Column(name = "bol_price")
    private Integer bolPrice;

    private String pg;

    @Column(name = "receipt_id")
    private String receiptId;

    @Column(name = "card_name")
    private String cardName;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "card_quota")
    private String cardQuota;

    @Column(name = "card_auth_no")
    private String cardAuthNo;

    @Column(name = "pay_method")
    private String payMethod;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime;

    @Transient
    Integer count;

    @Transient
    String installment;

    @Transient
    String autoKey;

    @Transient
    String cardCode;

}
