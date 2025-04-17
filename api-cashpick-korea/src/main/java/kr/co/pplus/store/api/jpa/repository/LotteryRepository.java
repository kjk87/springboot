package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Lottery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryRepository extends JpaRepository<Lottery, Long> {

    Lottery findBySeqNo(Long seqNo);

    Lottery findFirstByStatusAndEventStartDatetimeLessThanEqualAndEventEndDatetimeGreaterThanEqualOrderBySeqNoDesc(String status, String startDate, String endDate);

    Lottery findFirstByLotteryRoundAndAnnounceDatetimeLessThanEqual(Integer lotteryRound, String date);

    Lottery findFirstByLotteryRound(Integer lotteryRound);
}