package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsReviewDetailRepository extends JpaRepository<GoodsReviewDetail, Long> {

    GoodsReviewDetail findBySeqNo(Long seqNo);
    Page<GoodsReviewDetail> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);
    Page<GoodsReviewDetail> findAllByGoodsSeqNo(Long goodsSeqNo, Pageable pageable);
    Page<GoodsReviewDetail> findAllByGoodsPriceSeqNo(Long goodsPriceSeqNo, Pageable pageable);
    Page<GoodsReviewDetail> findAllByMemberSeqNoAndGoodsSeqNo(Long memberSeqNo, Long goodsSeqNo, Pageable pageable);
    Page<GoodsReviewDetail> findAll(Pageable pageable);
    Page<GoodsReviewDetail> findAllByPageSeqNo(Long pageSeqNo, Pageable pageable) ;
}