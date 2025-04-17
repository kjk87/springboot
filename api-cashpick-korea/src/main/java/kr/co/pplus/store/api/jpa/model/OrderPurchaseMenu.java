package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name="orderPurchaseMenu")
@Table(name="order_purchase_menu")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPurchaseMenu {

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

    @Column(name = "expire_datetime")
    private Date expireDatetime;

    @Transient
    List<OrderPurchaseMenuOption> orderPurchaseMenuOptionList;

}
