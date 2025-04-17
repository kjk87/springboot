package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    ProductReview findBySeqNo(Long seqNo);

    Page<ProductReview> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);

    Page<ProductReview> findAllByProductSeqNo(Long productSeqNo, Pageable pageable);
    Page<ProductReview> findAllByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable);
    Page<ProductReview> findAllByPageSeqNo(Long pageSeqNo, Pageable pageable);
    ProductReview findFirstByPageSeqNoOrderBySeqNoDesc(Long pageSeqNo);

    Integer countByProductPriceSeqNo(Long productPriceSeqNo);

    Integer countByMemberSeqNo(Long memberSeqNo);
    Integer countByPageSeqNo(Long pageSeqNo);

    @Query(value="select count(1) from product_review where eval = :eval and product_price_seq_no = :productPriceSeqNo ", nativeQuery = true)
    Integer findProductReviewCountGroupByEval(Long productPriceSeqNo, Integer eval);

    @Query(value="select count(1) from product_review where eval = :eval and page_seq_no = :pageSeqNo ", nativeQuery = true)
    Integer findProductReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo, Integer eval);

}