package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LotteryJoinCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryJoinCountRepository extends JpaRepository<LotteryJoinCount, String> {

    @Query(value = "SELECT seq_no, join_type, count(1) as join_count FROM pplus.lottery_join where member_seq_no = :memberSeqNo group by join_type", nativeQuery = true)
    List<LotteryJoinCount> countGroupByJoinType(Long memberSeqNo);

}