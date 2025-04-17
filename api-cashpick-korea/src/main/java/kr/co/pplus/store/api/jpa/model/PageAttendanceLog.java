package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="PageAttendanceLog") // This tells Hibernate to make a table out of this class
@Table(name="page_attendance_log")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAttendanceLog {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo;

    @Column(name="page_attendance_seq_no")
    Long pageAttendanceSeqNo = null ;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    String regDatetime;

}
