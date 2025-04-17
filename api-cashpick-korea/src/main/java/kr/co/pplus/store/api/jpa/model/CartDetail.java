package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;

@Entity(name="cartDetail")
@Table(name="cart")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDetail  {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;

    @Key
    @Column(name="page_seq_no")
    private Long pageSeqNo;

    @Key
    @Column(name="member_seq_no")
    private Long memberSeqNo;

    @Key
    @Column(name="order_menu_seq_no")
    private Long orderMenuSeqNo;

    @Column(name="sales_type")
    private Integer salesType; // 1:매장, 2:배달, 3:배송, 4:예약, 5:포장

    private Integer amount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    private String modDatetime;


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_menu_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    OrderMenu orderMenu = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @OrderBy("seqNo ASC")
    private Set<CartOption> cartOptionList;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;
}
