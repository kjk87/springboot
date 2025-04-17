package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Set;

@Data
@Table(name = "lucky_coupon")
@Entity(name="luckyCoupon")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "title")
    private String title;

    @Column(name = "use_target")
    private String useTarget;

    @Column(name = "type")
    private String type;//유형(discount, exchange)

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "valid_datetime")
    private String validDatetime;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "status")
    private Integer status;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "create_datetime", updatable = false)
    private String createDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "update_datetime")
    private String updateDatetime;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="coupon_seq_no", referencedColumnName="seq_no", insertable=false, updatable=false)
    private Set<LuckyCouponItem> luckyCouponItemList;
}
