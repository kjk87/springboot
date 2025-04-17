package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "lottery")
@Table(name = "lottery")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Lottery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lottery_round")
    private Integer lotteryRound;
    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_start_datetime")
    private String eventStartDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_end_datetime")
    private String eventEndDatetime;
    private String status; // active, before, expire, complete, inactive

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "announce_datetime")
    private String announceDatetime;
    private Integer no1;
    private Integer no2;
    private Integer no3;
    private Integer no4;
    private Integer no5;
    private Integer no6;
    @Column(name = "bonus_no")
    private Integer bonusNo;
    @Column(name = "first_type")
    private String firstType; // point, lotto
    @Column(name = "first_money")
    private Integer firstMoney;
    @Column(name = "second_type")
    private String secondType;
    @Column(name = "second_money")
    private Integer secondMoney;
    @Column(name = "third_type")
    private String thirdType;
    @Column(name = "third_money")
    private Integer thirdMoney;
    @Column(name = "forth_type")
    private String forthType;
    @Column(name = "forth_money")
    private Integer forthMoney;
    @Column(name = "fifth_type")
    private String fifthType;
    @Column(name = "fifth_money")
    private Integer fifthMoney;
    @Column(name = "first_add")
    private Integer firstAdd;
    @Column(name = "second_add")
    private Integer secondAdd;
    @Column(name = "third_add")
    private Integer thirdAdd;
    @Column(name = "forth_add")
    private Integer forthAdd;
    @Column(name = "fifth_add")
    private Integer fifthAdd;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDate;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

}
