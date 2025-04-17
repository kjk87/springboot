package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "pointBuy")
@Table(name = "point_buy")
public class PointBuy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    private Integer cash;
    private Integer status;
    private String pg;
    @Column(name = "card_name")
    private String cardName;
    @Column(name = "card_no")
    private String cardNo;
    @Column(name = "card_quota")
    private String cardQuota;

    @Column(name = "card_auth_no")
    private String cardAuthNo;

    @Column(name = "receipt_id")
    private String receiptId;

    @Column(name = "pay_method")
    private String payMethod;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_holder")
    private String accountHolder;

    @Column(name = "account")
    private String account;

    @Column(name = "expire_date")
    private String expireDate;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "cash_result")
    private String cashResult;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Transient
    Integer count;

    @Transient
    String installment;

    @Transient
    String autoKey;

    @Transient
    String cardCode;

}
