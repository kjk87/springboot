package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity(name = "luckyBox")
@Table(name = "luckybox")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBox implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    private String title;
    @Column(name = "engage_price")
    private Integer engagePrice;

    @Column(name = "refund_bol")
    private Integer refundBol;

    @Column(name = "provide_bol")
    private Integer provideBol;

    private Integer array;
    private String status;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", insertable=false, updatable=false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", insertable=false, updatable=false)
    private String modDatetime;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="luckybox_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    private Set<LuckyBoxEntry> entryList;
}
