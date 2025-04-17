package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "todayPickExample")
@Table(name = "today_pick_example")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickExample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;
    @Column(name = "today_pick_question_seq_no")
    private Long todayPickQuestionSeqNo;
    private String example;
    private Integer array;
    @Column(name = "join_count")
    private Integer joinCount;
    private Boolean temp;

}
