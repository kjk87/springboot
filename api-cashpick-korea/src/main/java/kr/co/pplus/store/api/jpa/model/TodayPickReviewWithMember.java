package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "todayPickReviewWithMember")
@Table(name = "today_pick_review")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickReviewWithMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;

    private String status;//active, inactive
    private String review;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;
}
