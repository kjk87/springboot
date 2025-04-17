package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="pointClickReward")
@Table(name="point_click_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PointClickReward implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "transaction_key")
    private String transactionKey;

    @Column(name = "placement_uid")
    private String placementUid;

    @Column(name = "ad_key")
    private String adKey;

    @Column(name = "ad_name")
    private String adName;

    @Column(name = "ad_profit")
    private Float adProfit;

    @Column(name = "ad_currency")
    private String adCurrency;

    private Float point;

    @Column(name = "device_ifa")
    private String deviceIfa;

    @Column(name = "picker_uid")
    private String pickerUid;


}
