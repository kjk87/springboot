package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LocationServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LocationServiceLogRepository extends JpaRepository<LocationServiceLog, Long> {

    void deleteAllById(String id);

}