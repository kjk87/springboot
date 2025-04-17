package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsCategory;
import kr.co.pplus.store.api.jpa.model.PageGoodsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageGoodsCategoryRepository extends JpaRepository<PageGoodsCategory, Long> {

    void deleteBySeqNo(Long seqNo) ;
    PageGoodsCategory findBySeqNo(Long seqNo) ;
    Page<PageGoodsCategory> findAll(Pageable pageable);
}