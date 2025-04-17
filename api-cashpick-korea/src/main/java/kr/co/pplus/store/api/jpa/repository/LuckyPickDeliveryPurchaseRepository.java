package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickDeliveryPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickDeliveryPurchaseRepository extends JpaRepository<LuckyPickDeliveryPurchase, Long> {

    LuckyPickDeliveryPurchase findBySeqNo(Long seqNo);
    LuckyPickDeliveryPurchase findByOrderNo(String orderNo);
}