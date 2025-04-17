package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

@Entity(name="orderPurchaseMenuWithCount")
@Table(name="order_purchase_menu")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPurchaseMenuWithCount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;
    @Column(name="order_purchase_seq_no")
    private Long orderPurchaseSeqNo;
    @Column(name="order_menu_seq_no")
    private Long orderMenuSeqNo;

    private Float price;

    @Column(name="option_price")
    private Float optionPrice;

    private Integer amount;

    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "expire_datetime")
    private String expireDatetime;

    @Column(name="menu_count")
    private Integer menuCount;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_menu_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    OrderMenu orderMenu = null ;


}
