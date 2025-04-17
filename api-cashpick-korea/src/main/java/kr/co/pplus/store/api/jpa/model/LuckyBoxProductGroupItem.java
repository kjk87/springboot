package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBoxProductGroupItem")
@Table(name = "luckybox_product_group_item")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxProductGroupItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "luckybox_product_group_seq_no")
    private Long luckyBoxProductGroupSeqNo;

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    private Boolean temp;

    @Column(name = "product_name")
    private String productName;
    private Float price;
    private String image;

}
