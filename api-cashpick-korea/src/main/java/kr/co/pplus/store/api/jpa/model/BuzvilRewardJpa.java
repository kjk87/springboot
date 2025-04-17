package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="buzvilReward")
@Table(name="buzvil_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuzvilRewardJpa implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq_no;
    private String unit_id;
    private String transaction_id;
    private String user_id;
    private Long campaign_id;
    private String campaign_name;
    private String title;
    private Integer point;
    private Integer base_point;
    private Boolean is_media;
    private String revenue_type;
    private String action_type;
    private Long event_at;
    private String extra;
    private Float unit_price;
    private String custom;
    private String ifa;
    private Integer reward;
    private Boolean allow_multiple_conversions;


}
