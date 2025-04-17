package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickPurchaseRepository extends JpaRepository<LuckyPickPurchase, Long> {
    LuckyPickPurchase findBySeqNo(Long seqNo);
    LuckyPickPurchase findByOrderNo(String orderNo);

}