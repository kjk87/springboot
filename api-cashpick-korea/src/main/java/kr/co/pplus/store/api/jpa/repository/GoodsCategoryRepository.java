package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory, Long> {

    GoodsCategory findBySeqNo(Long seqNo) ;
    GoodsCategory findByDepthAndNameAndLang(Byte depth, String name, String lang) ;
    Page<GoodsCategory> findAll(Pageable pageable);
    Page<GoodsCategory> findAllByDepth(Byte depth, Pageable pageable);
    Page<GoodsCategory> findAllByParentSeqNoAndDepth(Long parentSeqNo, Byte depth, Pageable pageable) ;
}