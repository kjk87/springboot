package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "lotteryWinner")
@Table(name = "lottery_winner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotteryWinner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lottery_seq_no")
    private Long lotterySeqNo;
    @Column(name = "lottery_join_seq_no")
    private String lotteryJoinSeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    private Integer grade;

    @Column(name = "gift_type")
    private String giftType; // point, lotto
    private Integer money;
    private String status; // active, request, reRequest, return, complete

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "status_datetime")
    private String statusDatetime;

    @Column(name = "profile_image")
    private String profileImage;

    private String reason;

    @Column(name = "win_no1")
    private Boolean winNo1;

    @Column(name = "win_no2")
    private Boolean winNo2;

    @Column(name = "win_no3")
    private Boolean winNo3;

    @Column(name = "win_no4")
    private Boolean winNo4;

    @Column(name = "win_no5")
    private Boolean winNo5;

    @Column(name = "win_no6")
    private Boolean winNo6;

    @Column(name = "win_add")
    private Boolean winAdd;

    private Integer no1;
    private Integer no2;
    private Integer no3;
    private Integer no4;
    private Integer no5;
    private Integer no6;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "join_datetime")
    private String joinDatetime;

//    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name="lottery_join_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
//    LotteryJoin lotteryJoin = null ;
}
