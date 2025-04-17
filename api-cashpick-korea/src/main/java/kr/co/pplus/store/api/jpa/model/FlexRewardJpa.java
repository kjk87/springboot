package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="flexReward")
@Table(name="flex_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlexRewardJpa implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq_no;

    private String userkey;
    private String flexcode;
    private Integer publisher_price;
    private Integer user_price;
    private String ad_title;
    private String ad_division;


}
