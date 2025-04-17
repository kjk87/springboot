package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventReply;
import kr.co.pplus.store.api.jpa.model.EventReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface EventReplyRepository extends JpaRepository<EventReply, Long> {

    Page<EventReply> findAllByEventReviewSeqNoAndStatusOrderBySeqNoAsc(Long eventReviewSeqNo, Integer status, Pageable pageable);

    Page<EventReply> findAllByEventSeqNoAndEventWinSeqNoAndStatusOrderBySeqNoAsc(Long eventSeqNo, Integer eventWinSeqNo, Integer status, Pageable pageable);
    Page<EventReply> findAllByEventWinIdAndStatusOrderBySeqNoAsc(Long eventWinId, Integer status, Pageable pageable);

    EventReply findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}