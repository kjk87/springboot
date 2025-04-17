package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolProduct")
@Table(name = "lucky_bol_product")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "lucky_bol_seq_no")
    private Long luckyBolSeqNo;

    @Column(name = "product_seq_no")
    private Long productSeqNo;

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_price")
    private Float productPrice;

    @Column(name = "product_image")
    private String productImage;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable=false)
    private String regDatetime;

    @Column(name = "exchange_price")
    private Integer exchangePrice;

    private Boolean delegate;

    @Column(name = "effective_date")
    private String effectiveDate;

    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_price_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductPriceRef productPriceData;
}
