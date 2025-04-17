package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxDelivery;
import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxDeliveryRepository extends JpaRepository<LuckyBoxDelivery, Long> {

    LuckyBoxDelivery findBySeqNo(Long seqNo);
}