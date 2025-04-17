package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "luckyCouponSend")
@Table(name = "lucky_coupon_send")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyCouponSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "send_datetime")
    private String sendDateTime;

    private String target;//발송대상(all, target, choice)

    @Column(name = "coupon_seq_no")
    private Long couponSeqNo;

    @Column(name = "admin_memo")
    private String adminMemo;

    @Column(name = "target_gender")
    private String targetGender;

    @Column(name = "target_age")
    private String targetAge;

    @Column(name = "target_member")
    private String targetMember;

    private Integer count;

    @Column(name = "status")
    private String status;//발송상태(ready, continue, complete, cancel)

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "create_datetime")
    private String createDateTime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "update_datetime")
    private String updateDateTime;

    @ManyToOne
    @JoinColumn(name = "coupon_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    private LuckyCoupon luckyCoupon;

}
