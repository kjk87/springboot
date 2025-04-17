package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LotteryWinner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryWinnerRepository extends JpaRepository<LotteryWinner, Long> {

    Page<LotteryWinner> findAllByMemberSeqNoAndLotterySeqNoOrderByGradeAsc(Long memberSeqNo, Long lotterySeqNo, Pageable pageable);

    LotteryWinner findBySeqNo(Long seqNo);

    LotteryWinner findByLotteryJoinSeqNo(String lotteryJoinSeqNo);

    List<LotteryWinner> findTop100ByGiftTypeAndStatus(String giftType, String status);

    List<LotteryWinner> findTop50ByGiftTypeAndStatusAndMemberSeqNoAndLotterySeqNo(String giftType, String status, Long memberSeqNo, Long lotterySeqNo);

    long countByGiftTypeAndStatusAndMemberSeqNoAndLotterySeqNo(String giftType, String status, Long memberSeqNo, Long lotterySeqNo);
}