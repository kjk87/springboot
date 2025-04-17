package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "shoppingBrandItem")
@Table(name = "shopping_brand_item")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingBrandItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "shopping_brand_seq_no")
    private Long shoppingBrandSeqNo;
    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

}
