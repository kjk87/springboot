package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolWin")
@Table(name = "lucky_bol_win")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolWin implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name = "lucky_bol_seq_no")
    private Long luckyBolSeqNo;

    @Column(name = "lucky_bol_purchase_seq_no")
    private Long luckyBolPurchaseSeqNo;

    @Column(name = "lucky_bol_gift_seq_no")
    private Long luckyBolGiftSeqNo;

    private String status;

    private String impression;

    @Column(name = "gift_grade")
    private Integer giftGrade;

    @Column(name = "gift_name")
    private String giftName;

    @Column(name = "gift_price")
    private Integer giftPrice;

    @Column(name = "gift_image")
    private String giftImage;

    @Column(name = "win_type")
    private Integer winType;

    @Column(name = "win_number")
    private String winNumber;

    @Column(name = "unique_number")
    private String uniqueNumber;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable=false)
    private String regDatetime;

    @Column(name = "gift_tr_id")
    private String giftTrId;

    @Column(name = "gift_order_no")
    private String giftOrderNo;

    @Column(name = "gift_mobile_number")
    private String giftMobileNumber;

    @Formula("(select count(1) from lucky_bol_reply lr where lr.lucky_bol_win_seq_no = seq_no and lr.status = 1)")
    private Integer replyCount;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable = false, updatable = false)
    Member member = null ;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "lucky_bol_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private LuckyBol luckyBol;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "lucky_bol_gift_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private LuckyBolGift luckyBolGift;

}
