package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.sql.Time;

@Data
@Entity(name="pageTimeOff") // This tells Hibernate to make a table out of this class
@Table(name="page_time_off")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageTimeOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private Time start;
    private Time end;

}