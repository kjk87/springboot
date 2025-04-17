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
@Entity(name="subscriptionDownload")
@Table(name="subscription_download")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDownload implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @Column(name = "member_seq_no")
    private Long memberSeqNo;

    private Integer status; // 1:사용중, 2:사용완료, 3:기간만료

    @Column(name = "expire_date")
    private LocalDate expireDate; // 만료일

    @Column(name = "have_count")
    private Integer haveCount = 0; // 보유수

    @Column(name = "use_count")
    private Integer useCount = 0; // 사용수

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "complete_datetime")
    private String completeDatetime;

    private String name;

    @Column(name = "use_condition")
    private String useCondition;

    private String type;

    @Column(name = "have_price")
    private Integer havePrice = 0; // 총금액

    @Column(name = "use_price")
    private Integer usePrice = 0; // 사용금액

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    Member member = null ;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_price_seq_no",  referencedColumnName="seq_no", insertable=false, updatable=false)
    ProductPriceRef productPrice = null ;

}
