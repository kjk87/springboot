package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity(name = "eventGift") // This tells Hibernate to make a table out of this class
@Table(name = "event_gift")
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventGiftJpa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;


    @Column(name = "event_seq_no")
    Long eventSeqNo = null;

    @Column(name = "gift_type")
    String giftType = null;

    String title = null;

    @Column(name = "total_count")
    Integer totalCount = null;

    String alert = null;

    @Column(name = "lot_percent")
    Double lotPercent = null;

    Long price = null;

    @Column(name = "attach_seq_no")
    Long attachSeqNo = null;

    @Column(name = "remain_count")
    Integer remainCount = null;

    @Column(name = "win_order")
    String winOrder = null;

    @Column(name = "beta_code")
    String betaCode = null;

    @Column(name = "expire_date")
    Date expireDate = null;

    @Column(name = "time_type")
    String timeType = null;

    @Column(name = "day_type")
    String dayType = null;

    @Column(name = "start_time")
    String startTime = null;

    @Column(name = "end_time")
    String endTime = null;

    String days = null;

    @Column(name = "review_point")
    Integer reviewPoint = null;

    @Column(name = "review_present")
    Boolean reviewPresent = null;

    @Column(name = "gift_link")
    String giftLink = null;

    @Column(name = "auto_send")
    Boolean autoSend = null;

    @Column(name = "giftishow_seq_no")
    Long giftishowSeqNo = null;

    @Column(name = "gift_image_url")
    String giftImageUrl = null;

    Boolean best = null;

    Boolean temp = null;

    Boolean delivery = null;

    @Column(name = "save_buff")
    Boolean saveBuff = null;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "attach_seq_no", insertable = false, updatable = false)
    Attachment attachment = null; // '프로필 이미지',
}
