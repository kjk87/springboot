package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name="pageBusinessHours") // This tells Hibernate to make a table out of this class
@Table(name="page_business_hours")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageBusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private Integer day;//1:일요일
    @Column(name="open_time")
    private Time openTime;
    @Column(name="close_time")
    private Time closeTime;

}