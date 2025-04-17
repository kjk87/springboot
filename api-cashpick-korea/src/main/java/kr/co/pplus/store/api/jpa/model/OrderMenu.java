package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Entity(name="orderMenu")
@Table(name="order_menu")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenu implements Serializable {


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    @Column(name="group_seq_no")
    private Long groupSeqNo;
    private String title;
    @Column(name="menu_info")
    private String menuInfo;
    private Float price;

    @Column(name="is_sold_out")
    private Boolean isSoldOut;

    private Boolean delegate;

    @Column(name="sold_out_date")
    private Date soldOutDate;

    @Column(name="reg_datetime")
    private Date regDatetime;

    private Boolean deleted;

    @Column(name="origin_price")
    private Float originPrice;

    private Float discount;

    @Column(name="expire_type")
    private String expireType;//date, number

    @Column(name = "expire_date")
    private Date expireDate;

    @Column(name="remain_date")
    private Integer remainDate;

    private String type;//menu, ticket

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @OrderBy("seqNo ASC")
    private Set<OrderMenuImage> imageList;


}
