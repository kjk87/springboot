package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


@Entity(name = "reappayInfo")
@Table(name = "reappay_info")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReappayInfo implements Serializable {

    @Id
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expires_in")
    private Integer expiresIn;

    private String jti;

    @Column(name = "refresh_token")
    private String refreshToken;
    private String scop;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expire_datetime")
    private String expireDatetime;

}
