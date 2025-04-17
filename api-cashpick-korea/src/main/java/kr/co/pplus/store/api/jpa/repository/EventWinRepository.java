package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventWin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventWinRepository extends JpaRepository<EventWin, Long> {

    @Query(value = "select max(seq_no) from event_win  where event_seq_no = :eventSeqNo ", nativeQuery = true)
    Integer findMaxSeqNo(Long eventSeqNo);

    EventWin findByEventSeqNoAndSeqNo(Long eventSeqNo, Integer seqNo);

    Boolean existsByEventSeqNoAndMemberSeqNo(Long eventSeqNo, Long memberSeqNo);

}