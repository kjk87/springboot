package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PrepaymentLog;
import kr.co.pplus.store.api.jpa.model.PrepaymentPublishDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PrepaymentLogRepository extends JpaRepository<PrepaymentLog, Long> {
    List<PrepaymentLog> findAllByPrepaymentPublishSeqNoAndStatus(Long prepaymentPublishSeqNo, String status);

    PrepaymentLog findBySeqNo(Long seqNo);

    @Query(value = "select count(1) from prepayment_log "
            + " where prepayment_seq_no = :prepaymentSeqNo "
            + " and status = 'completed'",
            nativeQuery = true)
    Integer countByPrepaymentSeqNo(Long prepaymentSeqNo);

}