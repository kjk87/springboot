package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="prepaymentLog")
@Table(name="prepayment_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrepaymentLog {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="prepayment_seq_no")
    private Long prepaymentSeqNo;
    @Column(name="prepayment_publish_seq_no")
    private Long prepaymentPublishSeqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;
    @Column(name="use_price")
    private Float usePrice;
    private String status; // request, completed
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="status_datetime")
    private String statusDatetime;
}
