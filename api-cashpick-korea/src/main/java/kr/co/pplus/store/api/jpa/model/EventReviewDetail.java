package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="eventReviewDetail") // This tells Hibernate to make a table out of this class
@Table(name="event_review")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventReviewDetail {

    public EventReviewDetail(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo = null ; //'구매 사용자 순번',

    @Column(name="event_seq_no", updatable = false)
    Long eventSeqNo = null ;

    @Column(name="event_win_seq_no", updatable = false)
    Integer eventWinSeqNo = null ;

    @Column(name="event_win_id", updatable = false)
    Long eventWinId = null ;

    @Column(name="event_gift_seq_no", updatable = false)
    Long eventGiftSeqNo = null ;

    @Column(name="review")
    String review = null ; //'구매 상품 리뷰',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    Integer status;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="event_review_seq_no", insertable = false, updatable = false)
    @Where(clause = "type = 'thumbnail'")
    private List<EventReviewImage> imageList = new ArrayList<EventReviewImage>();

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="event_win_id", referencedColumnName="id", insertable = false, updatable = false)
    EventWin eventWin  = null ;

    @Column(name="reply_count")
    private Integer replyCount;

    private Boolean friend;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ; //'구매 사용자 순번',

}
