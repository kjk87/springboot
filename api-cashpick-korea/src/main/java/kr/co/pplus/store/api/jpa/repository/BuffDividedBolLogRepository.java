package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buff;
import kr.co.pplus.store.api.jpa.model.BuffDividedBolLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffDividedBolLogRepository extends JpaRepository<BuffDividedBolLog, Long> {

    Page<BuffDividedBolLog> findAllByBuffSeqNoAndMoneyType(Long buffSeqNo, String moneyType, Pageable pageable);

}
