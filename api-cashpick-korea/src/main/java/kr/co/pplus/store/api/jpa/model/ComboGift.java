package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity(name = "comboGift")
@Table(name = "combo_gift")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboGift implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "event_month")
    private String eventMonth;

    @Column(name = "month_unique")
    private String monthUnique;
    private String type; // bol, coin, point, cash
    private String image;
    private Integer price;
    private String status; // active, inactive, complete

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="combo_gift_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    @OrderBy("seq_no ASC")
    private Set<ComboWin> winList;
}
