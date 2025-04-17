package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="pageGoodsCategoryDetail") // This tells Hibernate to make a table out of this class
@Table(name="page_goods_category")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageGoodsCategoryDetail {


    public PageGoodsCategoryDetail(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; //페이지 상품 카테고리 순번,

    @Column(name="page_seq_no")
    Long pageSeqNo = null ; //페이지 순번

    @Column(name="goods_category_seq_no")
    Long goodsCategorySeqNo  = null ;

    @OneToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="goods_category_seq_no", insertable = false, updatable = false)
    GoodsCategory goodsCategory  = null ;

    @Column(name="goods_count", insertable = false, updatable = false)
    Long goodsCount = null ;
}
