package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity(name = "luckyCouponItem")
@Table(name = "lucky_coupon_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyCouponItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "coupon_seq_no")
    private Long couponSeqNo;

    @Column(name = "product_seq_no")
    private Long productSeqNo;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "origin_price")
    private Float originPrice;

    @Column(name = "price")
    private Float price;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "create_datetime", updatable = false)
    private String createDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "update_datetime")
    private String updateDatetime;

}
