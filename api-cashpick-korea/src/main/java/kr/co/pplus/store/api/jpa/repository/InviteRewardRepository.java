package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyWithDate;
import kr.co.pplus.store.api.jpa.model.InviteReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface InviteRewardRepository extends JpaRepository<InviteReward, Long> {

    List<InviteReward> findAllByMemberSeqNoOrderBySeqNoDesc(Long memberSeqNo);

    InviteReward findBySeqNo(Long seqNo);

}