package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageGoodsCategory;
import kr.co.pplus.store.api.jpa.model.PageGoodsInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageGoodsInfoRepository extends JpaRepository<PageGoodsInfo, Long> {

    @Modifying
    void deleteByPageSeqNoAndGoodsSeqNo(Long pageSeqNo, Long goodsSeqNo) ;
    PageGoodsInfo findByPageSeqNoAndGoodsSeqNo(Long pageSeqNo, Long goodsSeqNo) ;
}