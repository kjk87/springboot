package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "visitLog")
@Table(name = "visit_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "page_seq_no")
    private Long pageSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private String status; // request, completed, reject
    private String note;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="status_datetime")
    private String statusDatetime;
    private String type;
}
