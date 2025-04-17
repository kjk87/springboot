package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolReplyOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolReplyOnlyRepository extends JpaRepository<LuckyBolReplyOnly, Long> {

    LuckyBolReplyOnly findBySeqNo(Long seqNo);
}