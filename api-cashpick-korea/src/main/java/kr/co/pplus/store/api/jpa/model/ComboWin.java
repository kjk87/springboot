package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity(name = "comboWin")
@Table(name = "combo_win")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboWin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    @Column(name = "combo_gift_seq_no")
    private Long comboGiftSeqNo;
    @Column(name = "combo_gift_name")
    private String comboGiftName;

    @Column(name = "month_unique")
    private String monthUnique;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Column(name = "start_join_seq_no")
    private Long startJoinSeqNo;

    @Column(name = "last_join_seq_no")
    private Long lastJoinSeqNo;

    @Column(name = "gift_type")
    private String giftType; // point, cash, bol, coin

    @Column(name = "gift_price")
    private BigDecimal giftPrice;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;


}
