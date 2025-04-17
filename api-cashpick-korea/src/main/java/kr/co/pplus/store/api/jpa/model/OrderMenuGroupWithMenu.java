package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity(name = "orderMenuGroupWithMenu")
@Table(name = "order_menu_group")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuGroupWithMenu implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private String name;
    private Integer array;
    private Boolean deleted;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @Where(clause = "deleted = false and is_sold_out = false and (sold_out_date != curdate() or sold_out_date is null) ")
    @OrderBy("delegate DESC, seq_no asc")
    private Set<OrderMenu> orderMenuList ;
}
