package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.WholesaleBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface WholesaleBalanceRepository extends JpaRepository<WholesaleBalance, Long> {

    @Query("select sum(w.advertiseFee) from wholesaleBalance w where w.agentSeqNo = :agentSeqNo")
    Float getSumAdvertiseFee(Long agentSeqNo);
}
