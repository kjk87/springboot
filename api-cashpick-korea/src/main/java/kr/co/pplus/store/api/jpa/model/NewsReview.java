package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name="newsReview")
@Table(name="news_review")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsReview {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "news_seq_no")
    private Long newsSeqNo;
    @Column(name = "page_seq_no")
    private Long pageSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private String review;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    private Boolean deleted;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne()
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private Member member;
}
