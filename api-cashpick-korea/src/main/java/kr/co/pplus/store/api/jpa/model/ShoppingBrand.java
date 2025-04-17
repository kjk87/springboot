package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "shoppingBrand")
@Table(name = "shopping_brand")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingBrand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "shopping_brand_category_seq_no")
    private Long shoppingBrandCategorySeqNo;

    private String title;
    private String note;
    private String status; // active, deactive

    private String image;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    private Integer array;

    @OneToOne
    @JoinColumn(name = "shopping_brand_category_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ShoppingBrandCategory category;

    @Transient
    private List<ProductPrice> productPriceList = new ArrayList<>();

    @Transient
    private Long totalProductPriceElements;

}
