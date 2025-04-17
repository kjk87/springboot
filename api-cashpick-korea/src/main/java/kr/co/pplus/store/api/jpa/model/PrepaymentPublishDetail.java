package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="prepaymentPublishDetail")
@Table(name="prepayment_publish")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrepaymentPublishDetail {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="prepayment_seq_no")
    private Long prepaymentSeqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    @Column(name="member_seq_no")
    private Long memberSeqNo;
    @Column(name="agent_seq_no")
    private Long agentSeqNo;
    private String status; // request, normal, completed, expired

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_date")
    private String expireDate;
    private Float price;
    @Column(name="add_price")
    private Float addPrice;
    @Column(name="total_price")
    private Float totalPrice;
    private String notice;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;
    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="completed_datetime")
    private String completedDatetime;
    @Column(name="have_price")
    private Float havePrice;
    @Column(name="use_price")
    private Float usePrice;

    @Column(name = "wholesale_code")
    private String wholesaleCode;
    @Column(name = "distributor_code")
    private String distributorCode;

    @Column(name="page_commission_ratio")
    private Float pageCommissionRatio;
    @Column(name="wholesale_commission_ratio")
    private Float wholesaleCommissionRatio;
    @Column(name="distributor_commission_ratio")
    private Float distributorCommissionRatio;

    @Column(name="page_commission")
    private Float pageCommission;
    @Column(name="wholesale_commission")
    private Float wholesaleCommission;
    @Column(name="distributor_commission")
    private Float distributorCommission;

    @Column(name="recommended_member_seq_no")
    private Long recommendedMemberSeqNo;

    @Column(name="recommended_member_type")
    private String recommendedMemberType;

    @Column(name="recommended_member_point")
    private Float recommendedMemberPoint;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    PageRefDetail page = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="prepayment_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    Prepayment prepayment = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    Member member = null ;
}
