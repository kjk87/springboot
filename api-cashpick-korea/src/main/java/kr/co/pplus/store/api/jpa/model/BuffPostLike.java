package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="buffPostLike")
@Table(name="buff_post_like")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name="buff_post_seq_no")
    private Long buffPostSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

}
