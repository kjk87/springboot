package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name="buffPostLikeNative")
@Table(name="buff_post_like")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostLikeNative {

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

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name = "is_friend")
    private Boolean isFriend;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Member member;

}
