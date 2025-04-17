package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity(name="giftishowBuyDetail")
@Table(name="giftishow_buy")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GiftishowBuyDetail {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
	private Long seqNo ;

    private String status = null ;

    @Column(name="member_seq_no")
    private Long memberSeqNo = null ;

    @Column(name="giftishow_seq_no")
    private Long giftishowSeqNo = null ;

    @Column(name="total_count")
    private Integer totalCount = null ;

    @Column(name="unit_price")
    private Integer unitPrice = null ;

    private Integer price = null ;

    private String msg = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime = null ;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="giftishow_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    Giftishow giftishow;

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="giftishow_buy_seq_no", insertable = false, updatable = false)
    List<GiftishowTarget> targetList;

}
