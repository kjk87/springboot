package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buff")
@Table(name = "buff")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Buff implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    private String title;

    private String info;

    private Integer capacity;//정원

    private String image;

    private Long owner;//그룹장
    private Long launchers;//개설자

    @Column(name = "total_divided_bol")
    private Double totalDividedBol;

    @Column(name = "total_divided_point")
    private Double totalDividedPoint;

    private Boolean deleted;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

}
