package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="smaadReward")
@Table(name="smaad_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmaadReward implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String user;
    private String adid;
    private String title;

    @Column(name = "orders_id")
    private String ordersId;

    @Column(name = "install_id")
    private String installId;

    private Integer pay;

    @Column(name = "user_pay")
    private Integer userPay;

    @Column(name = "user_pay2")
    private Integer userPay2;

    @Column(name = "time_utc")
    private String timeUtc;

    @Column(name = "time_jst")
    private String timeJst;


    private Integer approved;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "network_zone_id")
    private String networkZoneId;

    private Integer amount;

}
