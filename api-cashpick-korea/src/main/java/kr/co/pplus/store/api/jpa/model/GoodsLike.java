package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Entity(name="goodsLike") // This tells Hibernate to make a table out of this class
@Table(name="goods_like")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@IdClass(GoodsLikeKey.class)
public class GoodsLike {


    public GoodsLike(){

    }

    @Id
    @Column(name = "member_seq_no", nullable = false)
    Long memberSeqNo = null ;

    @Id
    @Column(name = "page_seq_no", nullable = false)
    Long pageSeqNo = null ;

    @Id
    @Column(name = "goods_seq_no", nullable = false)
    Long goodsSeqNo = null ;

    @Column(name = "goods_price_seq_no", nullable = false)
    Long goodsPriceSeqNo = null ;

    @Column(name="status")
    Integer status = 0 ; // 0 : not like, 1: like(찜)

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="expire_datetime", updatable = false)
    String expireDatetime  = null ; //'유효기간',
}
