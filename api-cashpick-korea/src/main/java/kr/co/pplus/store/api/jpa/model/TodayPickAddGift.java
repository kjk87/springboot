package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Data
@Entity(name = "todayPickAddGift")
@Table(name = "today_pick_add_gift")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodayPickAddGift {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "today_pick_seq_no")
    private Long todayPickSeqNo;
    @Column(name = "gift_type")
    private String giftType; // goods, money, bol, cash, point, coin
    private String title;
    private Integer price;
    @Column(name = "total_count")
    private Integer totalCount;
    @Column(name = "gift_image")
    private String giftImage;
    @Column(name = "gift_link")
    private String giftLink;
    private String alert;
    @Column(name = "review_present")
    private Boolean reviewPresent;
    private Integer array;
    private Boolean temp;

}
