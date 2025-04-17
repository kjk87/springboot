package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CashHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface CashHistoryRepository extends JpaRepository<CashHistory, Long> {

    Page<CashHistory> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);

    CashHistory findBySeqNo(Long seqNo);

}