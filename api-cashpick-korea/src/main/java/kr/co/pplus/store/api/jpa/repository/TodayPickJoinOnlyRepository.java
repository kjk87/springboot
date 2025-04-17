package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TodayPickJoinOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickJoinOnlyRepository extends JpaRepository<TodayPickJoinOnly, Long> {

    TodayPickJoinOnly findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "update today_pick_join set is_confirm = true where seq_no = :seqNo", nativeQuery = true)
    void updateConfirm(@Param("seqNo") Long seqNo);
}
