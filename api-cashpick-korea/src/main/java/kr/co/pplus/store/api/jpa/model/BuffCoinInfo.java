package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buffCoinInfo")
@Table(name = "buff_coin_info")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffCoinInfo implements Serializable {

    @Id
    @Column(name = "seq_no")
    private Long seqNo;

    private Double btc;

    private Integer krw;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

}
