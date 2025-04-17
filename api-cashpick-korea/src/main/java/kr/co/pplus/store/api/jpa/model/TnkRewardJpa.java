package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="tnkReward")
@Table(name="tnk_reward")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TnkRewardJpa implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq_no;

    private String seq_id;
    private Integer pay_pnt;
    private String md_user_nm;
    private String app_id;
    private Long pay_dt;
    private String app_nm;


}
