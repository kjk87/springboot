package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickReplyOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickReplyOnlyRepository extends JpaRepository<LuckyPickReplyOnly, Long> {

    LuckyPickReplyOnly findBySeqNo(Long seqNo);
}