package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBoxProductGroup")
@Table(name = "luckybox_product_group")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxProductGroup implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String title;
    @Column(name = "turn_no")
    private Integer turnNo;
    private Integer priority;
    private String status;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", insertable=false, updatable=false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", insertable=false, updatable=false)
    private String modDatetime;

}
