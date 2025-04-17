package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity(name = "todayPickQuestion")
@Table(name = "today_pick_question")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;
    private String question;
    private String hint;
    private Integer array;
    private String status; // active, cancel(무효)

    @Column(name = "answer", updatable = false, insertable = false)
    private Long answer; // 정답

    private String reason; // 무효사유
    private String image;

    @Column(name = "explain_text")
    private String explainText;

    @Column(name = "explain_image")
    private String explainImage;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "today_pick_question_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    @OrderBy(value = "array asc")
    private Set<TodayPickExample> exampleList;


}
