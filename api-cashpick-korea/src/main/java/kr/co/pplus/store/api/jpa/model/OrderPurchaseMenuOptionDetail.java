package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity(name="orderPurchaseMenuOptionDetail")
@Table(name="order_purchase_menu_option")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPurchaseMenuOptionDetail {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;
    @Column(name="order_purchase_menu_seq_no")
    private Long orderPurchaseMenuSeqNo;
    @Column(name="menu_option_detail_seq_no")
    private Long menuOptionDetailSeqNo;

    private Integer type; // 1:필수, 2:선택

    private Float price;

    private String title;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "menu_option_detail_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private MenuOptionDetail menuOptionDetail;
}
