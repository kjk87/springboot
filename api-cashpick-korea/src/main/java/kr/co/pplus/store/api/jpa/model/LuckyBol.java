package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBol")
@Table(name = "lucky_bol")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    private Boolean aos;
    private Boolean ios;

    @Column(name = "target_type")
    private String targetType; // all, supporter

    @Column(name = "engage_type")
    private String engageType; // bol,purchase

    @Column(name = "bol_type")
    private String bolType; // free,bol

    private String title;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "start_datetime")
    private String startDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "end_datetime")
    private String endDatetime;

    @Column(name = "engage_price")
    private Integer engagePrice;

    @Column(name = "engage_bol")
    private Integer engageBol;


    @Column(name = "total_engage")
    private Integer totalEngage;

    @Column(name = "engage_number")
    private Integer engageNumber;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "banner_image")
    private String bannerImage;

    @Column(name = "detail_image")
    private String detailImage;

    @Column(name = "announce_image")
    private String announceImage;

    private String status;

    @Column(name = "refund_type")
    private String refundType;

    private Integer refund;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "win_announce_datetime")
    private String winAnnounceDatetime;

    @Column(name = "live_url")
    private String liveUrl;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime", updatable = false)
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    private String modDatetime;

    @Column(name = "win_type")
    private Integer winType;

    private String first;
    private String second;
    private String third;

    @Formula("(select ifnull(sum(lbp.engaged_count),0) from lucky_bol_purchase lbp where lbp.lucky_bol_seq_no = seq_no and lbp.status = 'active')")
    private Integer joinCount;

}
