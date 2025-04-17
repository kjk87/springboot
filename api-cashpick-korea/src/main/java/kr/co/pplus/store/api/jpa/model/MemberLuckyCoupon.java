package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity(name = "memberLuckyCoupon")
@Table(name = "member_lucky_coupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberLuckyCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "coupon_seq_no")
    private Long couponSeqNo;

    @Column(name = "coupon_send_seq_no")
    private Long couponSendSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Column(name = "status")
    private Integer status;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "send_datetime")
    private String sendDateTime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "use_datetime")
    private String useDateTime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "create_datetime")
    private String createDateTime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "valid_datetime")
    private String validDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "update_datetime")
    private String updateDateTime;

    @ManyToOne
    @JoinColumn(name = "coupon_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    private LuckyCoupon luckyCoupon;

    @ManyToOne
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", updatable = false, insertable = false)
    private Member member;
}
