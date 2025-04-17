package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="luckyBolReplyOnly") // This tells Hibernate to make a table out of this class
@Table(name="lucky_bol_reply")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolReplyOnly {

    public LuckyBolReplyOnly(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="member_seq_no", updatable = false)
    Long memberSeqNo = null ; //'구매 사용자 순번',

    @Column(name="lucky_bol_win_seq_no", updatable = false)
    Long luckyBolWinSeqNo = null ;

    @Column(name="lucky_bol_review_seq_no", updatable = false)
    Long luckyBolReviewSeqNo = null ;

    String reply = null ; //'구매 상품 리뷰',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    Integer status = null;

}
