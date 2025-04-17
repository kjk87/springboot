package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity(name="pageBalance")
@Table(name="page_balance")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "issue_date")
    private String issueDate;
    @Column(name = "page_seq_no")
    private Long pageSeqNo;
    private Float advertise = 0f;
    private Float cashback = 0f;
    private String status; // ready, complete, hold
    private String  note;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "status_datetime")
    private String statusDatetime;

}
