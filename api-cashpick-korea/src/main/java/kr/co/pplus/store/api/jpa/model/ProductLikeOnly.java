package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="productLikeOnly") // This tells Hibernate to make a table out of this class
@Table(name="product_like")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductLikeOnly {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;

    @Column(name = "member_seq_no", nullable = false)
    Long memberSeqNo = null ;

    @Column(name = "product_price_seq_no", nullable = false)
    Long productPriceSeqNo = null ;

    @Column(name = "product_seq_no", nullable = false)
    Long productSeqNo = null ;

    @Column(name="status")
    Integer status = 0 ; // 0 : not like, 1: like(찜)

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_datetime", updatable = false)
    String expireDatetime  = null ; //'유효기간',
}
