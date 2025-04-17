package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventJoinJpa;
import kr.co.pplus.store.api.jpa.model.EventWin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventJoinRepository extends JpaRepository<EventJoinJpa, Long> {

    Integer countByMemberSeqNoAndEventSeqNoAndIsBuy(Long memberSeqNo, Long eventSeqNo, Boolean isBuy);


    @Query(value = "select max(seq_no) from event_join  where event_seq_no = :eventSeqNo ", nativeQuery = true)
    Integer findMaxSeqNo(Long eventSeqNo);

    List<EventJoinJpa> findAllByEventSeqNo(Long eventSeqNo);

    List<EventJoinJpa> findAllByEventSeqNoAndMemberSeqNoAndIsBuyOrderByJoinDatetimeDesc(Long eventSeqNo, Long memberSeqNo, Boolean isBuy);

}