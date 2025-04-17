package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="pincruxReward")
@Table(name="pincrux_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PincruxRewardJpa implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq_no;

    private Integer appkey;

    private Integer pubkey;

    private String usrkey;

    private String app_title;

    private Integer coin;

    private String transid;

    private String resign_flag;


}
