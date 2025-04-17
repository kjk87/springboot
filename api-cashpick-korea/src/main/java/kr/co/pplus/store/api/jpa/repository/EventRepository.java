package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventJoinJpa;
import kr.co.pplus.store.api.jpa.model.EventJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface EventRepository extends JpaRepository<EventJpa, Long> {

    EventJpa findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "UPDATE event set join_count = join_count + 1 where seq_no = :seqNo", nativeQuery = true)
    void updateJoinCount(@Param("seqNo") Long seqNo);

    @Modifying
    @Query(value = "UPDATE event set end_datetime = CURRENT_TIMESTAMP where seq_no = :seqNo", nativeQuery = true)
    void updateEndDate(@Param("seqNo") Long seqNo);

    @Modifying
    @Query(value = "UPDATE event set win_announce_datetime=CURRENT_TIMESTAMP where seq_no = :seqNo", nativeQuery = true)
    void updateWinAnnounceDateTime(@Param("seqNo") Long seqNo);

    @Modifying
    @Query(value = "UPDATE event set status=:status where seq_no = :seqNo", nativeQuery = true)
    void updateStatus(@Param("seqNo") Long seqNo, @Param("status") String status);

    @Modifying
    @Query(value = "UPDATE event set priority=:priority where seq_no = :seqNo", nativeQuery = true)
    void updatePriority(@Param("seqNo") Long seqNo, @Param("priority") Integer priority);

    @Modifying
    @Query(value = "UPDATE event set winner_count=winner_count + 1 where seq_no = :seqNo", nativeQuery = true)
    void updateIncreaseWinnerCount(@Param("seqNo") Long seqNo);

    @Modifying
    @Query(value = "UPDATE event set join_type=:joinType, join_term=:joinTerm where code = :code", nativeQuery = true)
    void updateJoinTypeAndJoinTermByCode(@Param("code") String code, @Param("joinType") String joinType, @Param("joinTerm") Integer joinTerm);


    @Query(value = "select max(code) from event", nativeQuery = true)
    String findMaxCode();

    @Query(value = "select max(priority) from event", nativeQuery = true)
    Integer findMaxPriority();
}