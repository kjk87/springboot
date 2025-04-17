package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name="eventWinWithJoin") // This tells Hibernate to make a table out of this class
@Table(name="event_win")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventWinWithJoin implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_seq_no")
    Long eventSeqNo;

    @Column(name = "seq_no")
    Integer seqNo;

    @Column(name = "member_seq_no")
    Long memberSeqNo;

    @Column(name = "gift_seq_no")
    Long giftSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "win_datetime")
    String winDatetime;

    @Column(name= "impression")
    String impression ;

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name= "blind")
    Boolean blind ;

    @Column(name = "status")
    private String status;

    @Column(name = "open_status")
    private Boolean openStatus;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "gift_status")
    private Integer giftStatus;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "use_datetime")
    private String useDatetime;

    @Column(name = "gift_tr_id")
    private String giftTrId;

    @Column(name = "gift_order_no")
    private String giftOrderNo;

    @Column(name = "gift_mobile_number")
    private String giftMobileNumber;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "delivery_phone")
    private String deliveryPhone;

    @Column(name = "delivery_post_code")
    private String deliveryPostCode;

    @Column(name = "is_lotto")
    private Boolean isLotto;

    @Column(name = "gift_type")
    private String giftType;

    @Column(name = "gift_title")
    private String giftTitle;

    @Column(name = "event_join_seq_no")
    private Long eventJoinSeqNo;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="gift_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    EventGiftJpa eventGift = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="event_join_seq_no", referencedColumnName = "id", insertable = false, updatable = false)
    EventJoinWithLottoNumber eventJoin = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    Member member = null ;
}