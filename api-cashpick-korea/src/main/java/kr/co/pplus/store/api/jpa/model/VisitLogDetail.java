package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name = "visitLogDetail")
@Table(name = "visit_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitLogDetail {


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

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Member member;
}
