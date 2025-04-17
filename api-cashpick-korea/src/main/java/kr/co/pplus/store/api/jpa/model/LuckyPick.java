package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyPick")
@Table(name = "lucky_pick")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyPick implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    private Boolean android;
    private Boolean ios;

    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    private String endDatetime;

    @Column(name = "pick_ratio")
    private String pickRatio;

    @Column(name = "engage_price")
    private Integer engagePrice;

    @Column(name = "product_seq_no")
    private Long productSeqNo;

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private Float productPrice;

    private String image;
    private String status;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    private Integer array;

}
