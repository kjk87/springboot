package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity(name = "subscriptionLog")
@Table(name = "subscription_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "subscription_seq_no")
    private Long subscriptionSeqNo;

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Column(name = "use_price")
    private Integer usePrice;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_price_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    ProductPriceRef productPrice = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    SubscriptionDownload subscriptionDownload = null ;

}
