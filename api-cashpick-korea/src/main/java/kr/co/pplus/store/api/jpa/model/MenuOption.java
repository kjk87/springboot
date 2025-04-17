package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity(name="menuOption")
@Table(name="menu_option")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuOption implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;

    @Column(name="page_seq_no")
    private Long pageSeqNo ;

    private Integer type;
    private String title;

    private Integer array;

    @Column(name="is_sold_out")
    private Boolean isSoldOut;

    @Column(name="reg_datetime")
    private LocalDateTime regDatetime;

    private Boolean deleted;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch=FetchType.EAGER)
    @JoinColumn(name = "menu_option_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @Where(clause = "deleted = false")
    @OrderBy("seqNo ASC")
    private Set<MenuOptionDetail> detailList;


}
