package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolGift")
@Table(name = "lucky_bol_gift")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolGift implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lucky_bol_seq_no")
    private Long luckyBolSeqNo;
    private String type;
    private Integer grade;
    private String title;
    private Integer amount;
    private String notice;
    private Integer price;

    @Column(name = "remain_count", unique = true)
    private Integer remainCount;

    private String image;

    @Column(name = "gift_link")
    private String giftLink;

    @Column(name = "review_present")
    private Boolean reviewPresent;

    @Column(name = "giftishow_seq_no")
    private Long giftishowSeqNo;

    private Boolean temp;

    @Column(name = "auto_send")
    private Boolean autoSend;
}
