package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PurchaseProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseProductOptonRepository extends JpaRepository<PurchaseProductOption, Long> {

    List<PurchaseProductOption> findAllByPurchaseProductSeqNo(Long purchaseProductSeqNo);
}
