package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "lotteryJoinUser")
@Table(name = "lottery_join_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotteryJoinUser implements Serializable {

    @Id
    @Column(name = "seq_no")
    private String seqNo;
    @Column(name = "lottery_seq_no")
    private Long lotterySeqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    private Integer no1;
    private Integer no2;
    private Integer no3;
    private Integer no4;
    private Integer no5;
    private Integer no6;
    @Column(name = "join_type")
    private String joinType;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;
}
