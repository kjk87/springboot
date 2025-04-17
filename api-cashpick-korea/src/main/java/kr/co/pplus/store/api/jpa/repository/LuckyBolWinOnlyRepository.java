package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolWinOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolWinOnlyRepository extends JpaRepository<LuckyBolWinOnly, Long> {

    LuckyBolWinOnly findBySeqNo(Long seqNo);

}