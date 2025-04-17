package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity(name = "comboEvent")
@Table(name = "combo_event")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboEvent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String title;
    private Boolean aos;
    private Boolean ios;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_datetime")
    private String eventDatetime;

    @Column(name = "combo_event_array")
    private Long comboEventArray;
    private String hint;
    private String image;
    private String question;
    private String status; // active, inactive, complete(완료), expire(참여마감), cancel(무효처리)
    private String reason; // 무효이유
    private Long answer; // 정답 comboEventExample.seqNo


    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    @Formula("(select count(1) from combo_review cr where cr.combo_event_seq_no = seq_no)")
    private Integer replyCount;

    @Formula("(select count(1) from combo_join cj where cj.combo_event_seq_no = seq_no)")
    private Integer joinCount;


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="combo_event_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("array ASC")
    private Set<ComboEventExample> exampleList;

}
