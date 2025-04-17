package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Lottery;
import kr.co.pplus.store.api.jpa.model.LotteryOnlyRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryOnlyRoundRepository extends JpaRepository<LotteryOnlyRound, Long> {

    List<LotteryOnlyRound> findTop20ByStatusAndAnnounceDatetimeLessThanEqualAndLotteryRoundGreaterThanEqualOrderBySeqNoDesc(String status, String date, Integer lotteryRound);
    List<LotteryOnlyRound> findTop20ByStatusAndAnnounceDatetimeLessThanEqualOrderBySeqNoDesc(String status, String date);
}