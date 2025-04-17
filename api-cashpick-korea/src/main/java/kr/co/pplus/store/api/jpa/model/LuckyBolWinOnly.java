package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolWinOnly")
@Table(name = "lucky_bol_win")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolWinOnly implements Serializable {

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
}
