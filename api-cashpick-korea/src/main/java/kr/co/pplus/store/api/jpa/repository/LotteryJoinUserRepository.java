package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LotteryJoinUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LotteryJoinUserRepository extends JpaRepository<LotteryJoinUser, String> {

    int countByLotterySeqNoAndMemberSeqNo(Long lotterySeqNo, Long memberSeqNo);

    Page<LotteryJoinUser> findAllByLotterySeqNoAndMemberSeqNoOrderBySeqNoDesc(Long lotterySeqNo, Long memberSeqNo, Pageable pageable);

}