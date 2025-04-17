package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderMenuReview;
import kr.co.pplus.store.api.jpa.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderMenuReviewRepository extends JpaRepository<OrderMenuReview, Long> {

    OrderMenuReview findBySeqNo(Long seqNo);

    Page<OrderMenuReview> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);


    Page<OrderMenuReview> findAllByPageSeqNo(Long pageSeqNo, Pageable pageable);

    OrderMenuReview findFirstByPageSeqNoOrderBySeqNoDesc(Long pageSeqNo);

    Integer countByMemberSeqNo(Long memberSeqNo);
    Integer countByPageSeqNo(Long pageSeqNo);


    @Query(value="select count(1) from order_menu_review where eval = :eval and page_seq_no = :pageSeqNo ", nativeQuery = true)
    Integer findReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo, Integer eval);

}