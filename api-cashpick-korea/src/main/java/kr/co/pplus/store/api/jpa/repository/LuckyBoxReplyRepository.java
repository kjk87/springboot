package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventReply;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxReplyRepository extends JpaRepository<LuckyBoxReply, Long> {

    Page<LuckyBoxReply> findAllByLuckyBoxPurchaseItemSeqNoAndStatusOrderBySeqNoAsc(Long luckyBoxPurchaseItemSeqNo, Integer status, Pageable pageable);

    Page<LuckyBoxReply> findAllByLuckyBoxReviewSeqNoAndStatusOrderBySeqNoAsc(Long luckyBoxReviewSeqNo, Integer status, Pageable pageable);

    LuckyBoxReply findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}