package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LotteryJoinCount;
import kr.co.pplus.store.api.jpa.model.LotteryJoinUserCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryJoinUserCountRepository extends JpaRepository<LotteryJoinUserCount, String> {

    @Query(value = "SELECT seq_no, join_type, count(1) as join_count FROM pplus.lottery_join_user where lottery_seq_no = :lotterySeqNo and member_seq_no = :memberSeqNo group by join_type", nativeQuery = true)
    List<LotteryJoinUserCount> countGroupByJoinType(Long lotterySeqNo, Long memberSeqNo);

}