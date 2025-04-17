package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolReply;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolReplyRepository extends JpaRepository<LuckyBolReply, Long> {

    Page<LuckyBolReply> findAllByLuckyBolWinSeqNoAndStatusOrderBySeqNoAsc(Long luckyBolWinSeqNo, Integer status, Pageable pageable);

    Page<LuckyBolReply> findAllByLuckyBolReviewSeqNoAndStatusOrderBySeqNoAsc(Long luckyBolReviewSeqNo, Integer status, Pageable pageable);

    LuckyBolReply findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}