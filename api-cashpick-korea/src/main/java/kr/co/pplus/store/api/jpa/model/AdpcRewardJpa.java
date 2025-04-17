package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="adpcReward")
@Table(name="adpc_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdpcRewardJpa implements Serializable {

    @Id
    private String reward_key;

    private Integer quantity;

    private String campaign_key;


    @Convert(converter = JpaConverterDatetime.class)
    private String reg_datetime;

    private Long member_seq_no;




}
