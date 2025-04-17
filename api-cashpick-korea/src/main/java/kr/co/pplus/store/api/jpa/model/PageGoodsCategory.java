package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name="pageGoodsCategory") // This tells Hibernate to make a table out of this class
@Table(name="page_goods_category")
@EqualsAndHashCode(callSuper = false)
@Alias("PageGoodsCategory")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageGoodsCategory {


    public PageGoodsCategory(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; //페이지 상품 카테고리 순번,

    @Column(name="page_seq_no")
    Long pageSeqNo = null ; //페이지 순번

    @Column(name="goods_category_seq_no")
    Long goodsCategorySeqNo  = null ;
}
