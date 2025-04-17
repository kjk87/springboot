package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity(name="cartOption")
@Table(name="cart_option")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartOption {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;

    @Column(name="cart_seq_no")
    private Long cartSeqNo;

    @Column(name="menu_option_detail_seq_no")
    private Long menuOptionDetailSeqNo;

    private Integer type; // 필수여부


    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "menu_option_detail_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private MenuOptionDetail menuOptionDetail;

}
