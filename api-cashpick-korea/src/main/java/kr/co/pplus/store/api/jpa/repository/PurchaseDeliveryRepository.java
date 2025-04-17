package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PurchaseDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseDeliveryRepository extends JpaRepository<PurchaseDelivery, Long>{

    PurchaseDelivery findBySeqNo(Long seqNo);
    PurchaseDelivery findByPurchaseProductSeqNo(Long purchaseProductSeqNo);
}
