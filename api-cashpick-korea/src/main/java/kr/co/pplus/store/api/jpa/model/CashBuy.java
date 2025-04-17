package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="cashBuy")
@Table(name="cash_buy")
public class CashBuy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="order_id")
    private String orderId;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private Integer cash;
    private Integer status;
    private String pg;
    @Column(name="card_name")
    private String cardName;
    @Column(name="card_no")
    private String cardNo;
    @Column(name="card_quota")
    private String cardQuota;

    @Column(name="receipt_id")
    private String receiptId;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime;
}
