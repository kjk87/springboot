package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickReplyRepository extends JpaRepository<LuckyPickReply, Long> {

    Page<LuckyPickReply> findAllByLuckyPickPurchaseItemSeqNoAndStatusOrderBySeqNoAsc(Long luckyPickPurchaseItemSeqNo, Integer status, Pageable pageable);

    Page<LuckyPickReply> findAllByLuckyPickReviewSeqNoAndStatusOrderBySeqNoAsc(Long luckyPickReviewSeqNo, Integer status, Pageable pageable);

    LuckyPickReply findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}