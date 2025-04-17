package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="orderMenuOption")
@Table(name="order_menu_option")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuOption implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="menu_seq_no")
    private Long menuSeqNo;
    @Column(name="menu_option_seq_no")
    private Long menuOptionSeqNo;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "menu_option_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @Where(clause = "deleted = false and is_sold_out = false")
    MenuOption menuOption = null ;
}
