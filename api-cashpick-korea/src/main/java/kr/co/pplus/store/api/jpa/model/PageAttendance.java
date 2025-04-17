package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity(name="PageAttendance") // This tells Hibernate to make a table out of this class
@Table(name="page_attendance")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAttendance {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo;

    @Column(name="page_seq_no")
    Long pageSeqNo = null ;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="attendance_count")
    Integer attendanceCount = null ;

    @Column(name="total_count")
    Integer totalCount = null ;

    Integer status = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    String regDatetime;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;

}
