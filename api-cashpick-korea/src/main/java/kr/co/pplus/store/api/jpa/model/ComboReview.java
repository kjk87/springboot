package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "comboReview")
@Table(name = "combo_review")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboReview implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    @Column(name = "combo_event_seq_no")
    private Long comboEventSeqNo;
    @Column(name = "combo_join_seq_no")
    private Long comboJoinSeqNo;

    private String status;//active, inactive
    private String review;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;


}
