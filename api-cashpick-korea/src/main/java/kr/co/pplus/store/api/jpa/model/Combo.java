package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "combo")
@Table(name = "combo")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Combo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    @Column(name = "combo_join_seq_no")
    private Long comboJoinSeqNo;
    private String status; // win(당첨), cancel(무효)

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_date")
    private String startDate;

    @Column(name = "start_combo_event_array")
    private Long startComboEventArray;
    @Column(name = "last_combo_event_array")
    private Long lastComboEventArray;
    @Column(name = "combo_count")
    private Integer comboCount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;


}
