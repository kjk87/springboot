package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "firstServed")
@Table(name = "first_served")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FirstServed implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private Boolean aos;

    private Boolean ios;
    private String status; //active, inactive, complete, expire

    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    private String endDatetime;

    @Column(name = "gate_number")
    private String gateNumber;
    @Column(name = "quantity_type")
    private String quantityType; // none, limit
    @Column(name = "max_quantity")
    private Integer maxQuantity;
    @Column(name = "product_seq_no")
    private Long productSeqNo;
    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "status_datetime")
    private String statusDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;


    @Key
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_price_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    ProductPriceRef productPrice = null;

}
