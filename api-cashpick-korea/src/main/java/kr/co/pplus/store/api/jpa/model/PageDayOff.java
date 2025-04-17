package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.sql.Time;

@Data
@Entity(name="pageDayOff") // This tells Hibernate to make a table out of this class
@Table(name="page_day_off")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageDayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private Integer week;
    private Integer day;

}