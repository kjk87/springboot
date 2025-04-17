package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="eventReview") // This tells Hibernate to make a table out of this class
@Table(name="event_review")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventReview {

    public EventReview(){

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

    @Transient
    private List<EventReviewImage> imageList = new ArrayList<EventReviewImage>();

}
