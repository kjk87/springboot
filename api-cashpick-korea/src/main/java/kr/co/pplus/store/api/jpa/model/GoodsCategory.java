package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="goods_category") // This tells Hibernate to make a table out of this class
@Table(name="goods_category")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsCategory {


    public GoodsCategory(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '상품 카테고리 순번',

    @Column(name="parent_seq_no")
    Long parentSeqNo = null ; // '상위카테고리 순번',

    @Column(name="depth")
    Byte depth  = null ; //'상품 카테고리 depth',

    @Column(name="sort_num")
    Integer sortNum  = null ; //'정렬 순서',

    String name = null ; //'상품 카테고리 명',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ; //'등록시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="mod_datetime")
    String modDatetime  = null ; //'변경시각',

    @Column(name="lang")
    String lang  = "ko" ; //'상품명 등록 언어',
}
