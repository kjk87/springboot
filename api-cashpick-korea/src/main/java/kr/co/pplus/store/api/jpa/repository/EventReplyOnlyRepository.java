package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventReply;
import kr.co.pplus.store.api.jpa.model.EventReplyOnly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface EventReplyOnlyRepository extends JpaRepository<EventReplyOnly, Long> {

    EventReply findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}