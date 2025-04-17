package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "todayPickJoinOnly")
@Table(name = "today_pick_join")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickJoinOnly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "member_seq_no")
    private Long memberSeqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;

    private String status; // active, win

    @Column(name = "gift_type")
    private String giftType; // goods, money, point, cash, bol, coin

    @Column(name = "gift_price")
    private BigDecimal giftPrice;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "win_datetime")
    private String winDatetime;

    @Column(name = "is_confirm")
    private Boolean isConfirm;

    @Column(name = "add_status")
    private String addStatus; // none, win

    @Column(name = "add_gift_type")
    private String addGiftType; // goods, money, point, cash, bol, coin, mobileGift

    @Column(name = "add_gift_price")
    private BigDecimal addGiftPrice;

    @Column(name = "giftishow_goods_code")
    private Long giftishowGoodsCode;
    @Column(name = "is_send_giftishow")
    private Boolean isSendGiftishow;

    @Column(name = "referral_member_seq_no")
    private Long referralMemberSeqNo;

    @Transient
    private List<TodayPickJoinItem> joinItemList = new ArrayList<>();


}
