package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CashLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface CashLogRepository extends JpaRepository<CashLog, Long> {

    Page<CashLog> findAllByPageSeqNoAndType(Long pageSeqNo, String type, Pageable pageable);

}