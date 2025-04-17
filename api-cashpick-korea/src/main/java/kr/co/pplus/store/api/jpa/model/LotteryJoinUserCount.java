package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity(name = "lotteryJoinUserCount")
@Table(name = "lottery_join_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotteryJoinUserCount implements Serializable {

    @Id
    @Column(name = "seq_no")
    private String seqNo;

    @Column(name = "join_type")
    private String joinType;

    @Column(name = "join_count")
    private Integer joinCount;
}
