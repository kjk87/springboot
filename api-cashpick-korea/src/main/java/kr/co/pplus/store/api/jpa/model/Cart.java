package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity(name="cart")
@Table(name="cart")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cart {

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

    private Integer amount ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    private String modDatetime;

    @Transient
    List<CartOption> cartOptionList;
}
