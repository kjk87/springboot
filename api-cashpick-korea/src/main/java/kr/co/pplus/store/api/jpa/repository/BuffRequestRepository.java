package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffRequestRepository extends JpaRepository<BuffRequest, Long> {
    boolean existsByMemberSeqNoAndBuffSeqNoAndStatus(Long memberSeqNo, Long buffSeqNo, String status);

    List<BuffRequest> findAllByMemberSeqNoAndStatusOrderBySeqNoDesc(Long memberSeqNo, String status);

    int countByMemberSeqNoAndStatus(Long memberSeqNo, String status);

    BuffRequest findBySeqNo(Long seqNo);
}
