package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity(name = "todayPickJoinItem")
@Table(name = "today_pick_join_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickJoinItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;
    @Column(name = "today_pick_question_seq_no")
    private Long todayPickQuestionSeqNo;
    @Column(name = "today_pick_example_seq_no")
    private Long todayPickExampleSeqNo; // answer

    @Column(name = "today_pick_join_seq_no")
    private Long todayPickJoinSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    private String status; // active(참석), cancel(무효), win(당첨)
    private String example;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "join_datetime")
    private String joinDatetime;

}
