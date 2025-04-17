package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseItemOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxPurchaseItemOnlyRepository extends JpaRepository<LuckyBoxPurchaseItemOnly, Long> {

    List<LuckyBoxPurchaseItemOnly> findAllByStatusAndIsOpenAndOpenDatetimeLessThanEqualAndDeliveryStatusIsNull(Integer status, Boolean isOpen, String expireDate);

    LuckyBoxPurchaseItemOnly findBySeqNo(Long seqNo);

    List<LuckyBoxPurchaseItemOnly> findAllByDeliveryStatus(Integer status);

    List<LuckyBoxPurchaseItemOnly> findAllByLuckyBoxPurchaseSeqNo(Long luckyBoxPurchaseSeqNo);
}