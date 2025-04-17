package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="shoppingGroupItem")
@Table(name="shopping_group_item")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingGroupItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "shopping_group_seq_no")
    private Long shoppingGroupSeqNo;
    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_price_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductPriceRef productPrice;

}
