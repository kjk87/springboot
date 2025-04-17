package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxDeliveryPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxDeliveryPurchaseRepository extends JpaRepository<LuckyBoxDeliveryPurchase, Long> {

    LuckyBoxDeliveryPurchase findBySeqNo(Long seqNo);
    LuckyBoxDeliveryPurchase findByOrderNo(String orderNo);
}