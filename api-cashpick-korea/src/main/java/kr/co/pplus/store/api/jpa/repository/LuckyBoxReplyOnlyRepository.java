package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxReply;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReplyOnly;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxReplyOnlyRepository extends JpaRepository<LuckyBoxReplyOnly, Long> {

    LuckyBoxReplyOnly findBySeqNo(Long seqNo);
}