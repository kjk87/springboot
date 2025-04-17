package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="adsyncReward")
@Table(name="adsync_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdSyncRewardJpa implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq_no;

    private String partner;

    private String cust_id;

    private String ad_no;

    private String seq_id;


    private Integer point;

    private String ad_title;


}
