package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="buffPostReply")
@Table(name="buff_post_reply")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostReply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name="buff_post_seq_no")
    private Long buffPostSeqNo;

    private String reply;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    private Boolean deleted;
}
