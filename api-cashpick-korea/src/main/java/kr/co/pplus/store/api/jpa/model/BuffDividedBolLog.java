package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buffDividedBolLog")
@Table(name = "buff_divided_bol_log")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffDividedBolLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "buff_seq_no")
    private Long buffSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private String type;//event, lotto, shopping

    @Column(name = "money_type")
    private String moneyType;//bol, point

    @Column(name = "event_seq_no")
    private Long eventSeqNo;

    @Column(name = "shopping_seq_no")
    private Long shoppingSeqNo;

    private Float amount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ;
}
