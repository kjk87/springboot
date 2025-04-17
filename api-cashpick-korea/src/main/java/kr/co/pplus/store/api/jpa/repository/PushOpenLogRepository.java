package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PushOpenLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PushOpenLogRepository extends JpaRepository<PushOpenLog, Long> {


}