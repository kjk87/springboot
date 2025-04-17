package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "comboJoin")
@Table(name = "combo_join")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboJoin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "combo_event_seq_no")
    private Long comboEventSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    private String status; // active(참석), cancel(무효), win(당첨)
    @Column(name = "combo_event_example_seq_no")
    private Long comboEventExampleSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "join_datetime")
    private String joinDatetime;

    @Column(name = "combo_event_array")
    private Long comboEventArray;

    @Column(name = "join_unique")
    private String joinUnique;

}
