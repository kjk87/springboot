package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageGoodsCategory;
import kr.co.pplus.store.api.jpa.model.PageGoodsCategoryDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageGoodsCategoryDetailRepository extends JpaRepository<PageGoodsCategoryDetail, Long> {

    PageGoodsCategoryDetail findBySeqNo(Long seqNo) ;


    @Query(value = " select pgc.*, "
        +  " (select count(1) from goods g where g.page_seq_no = :pageSeqNo and g.category_seq_no = pgc.goods_category_seq_no and g.status =1) as goods_count \n"
        +  " from page_goods_category pgc \n"
        +  "                inner join goods_category gc on pgc.page_seq_no = :pageSeqNo and gc.seq_no = pgc.goods_category_seq_no "
        +  " where ISNULL(:depth) =1  or gc.depth = :depth ",
    nativeQuery=true)
    List<PageGoodsCategoryDetail> findAllBy(@Param("pageSeqNo") Long pageSeqNo, @Param("depth") Byte depth);
}