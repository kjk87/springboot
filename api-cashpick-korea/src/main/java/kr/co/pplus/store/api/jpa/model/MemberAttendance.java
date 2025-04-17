package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="MemberAttendance") // This tells Hibernate to make a table out of this class
@Table(name="member_attendance")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberAttendance {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo;

    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="attendance_count")
    Integer attendanceCount = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="attendance_datetime")
    String attendanceDatetime;

    @Transient
    Boolean isAttendance;

    @Transient
    Integer attendancePoint;

}
