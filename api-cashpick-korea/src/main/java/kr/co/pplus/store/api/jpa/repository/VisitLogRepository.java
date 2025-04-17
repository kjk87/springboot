package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    VisitLog findBySeqNo(Long seqNo);

    VisitLog findFirstByPageSeqNoAndMemberSeqNoAndStatus(Long pageSeqNo, Long memberSeqNo, String status);

    List<VisitLog> findAllByPageSeqNoAndMemberSeqNoAndStatus(Long pageSeqNo, Long memberSeqNo, String status);
}