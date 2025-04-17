package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "lotteryOnlyRound")
@Table(name = "lottery")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotteryOnlyRound implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lottery_round")
    private Integer lotteryRound;

    private String status; // active, before, expire, complete, inactive

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "announce_datetime")
    private String announceDatetime;

}
