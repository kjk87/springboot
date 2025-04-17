package kr.co.pplus.store.api.jpa.model;

import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="cashLog")
@Table(name="cash_log")
public class CashLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private String type; // charge, used
    private Integer cash;
    private String note;

    @Column(name="gave_member_seq_no")
    private Long gaveMemberSeqNo;

    @Column(name="gave_type")
    private String gaveType;//menu, prepayment, ticket

    @Column(name="gave_seq_no")
    private Long gaveSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime;

}
