package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventGiftJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventGiftRepository extends JpaRepository<EventGiftJpa, Long> {

    @Query(value="SELECT SUM(total_count) FROM event_gift WHERE event_seq_no=:eventSeqNo",
            nativeQuery = true)
    Integer sumTotalCount(Long eventSeqNo);

    @Query(value="SELECT SUM(remain_count) FROM event_gift WHERE event_seq_no=:eventSeqNo",
            nativeQuery = true)
    Integer sumRemainCount(Long eventSeqNo);

    List<EventGiftJpa> findAllByEventSeqNo(Long eventSeqNo);

    @Modifying
    @Query(value="update event_gift set remain_count=remain_count - 1 where event_seq_no = :eventSeqNo and seq_no = :seqNo and remain_count > 0", nativeQuery = true)
    void updateDecreaseRemainCount(@Param("eventSeqNo") Long eventSeqNo, @Param("seqNo") Long seqNo) ;
}