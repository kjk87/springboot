package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "lotteryWinCondition")
@Table(name = "lottery_win_condition")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotteryWinCondition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lottery_seq_no")
    private Long lotterySeqNo;

    @Column(name = "first_grade")
    private Integer firstGrade;
    @Column(name = "second_grade")
    private Integer secondGrade;
    @Column(name = "third_grade")
    private Integer thirdGrade;
    @Column(name = "forth_grade")
    private Integer forthGrade;
    @Column(name = "fifth_grade")
    private Integer fifthGrade;
    @Column(name = "first_money")
    private Integer firstMoney;
    @Column(name = "second_money")
    private Integer secondMoney;
    @Column(name = "third_money")
    private Integer thirdMoney;
    @Column(name = "forth_money")
    private Integer forthMoney;
    @Column(name = "fifth_money")
    private Integer fifthMoney;


}
