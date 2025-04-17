package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseItemOnly;
import kr.co.pplus.store.api.jpa.model.LuckyPickPurchaseItemOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickPurchaseItemOnlyRepository extends JpaRepository<LuckyPickPurchaseItemOnly, Long> {

    List<LuckyPickPurchaseItemOnly> findAllByStatusAndIsOpenAndOpenDatetimeLessThanEqual(Integer status, Boolean isOpen, String expireDate);

    LuckyPickPurchaseItemOnly findBySeqNo(Long seqNo);

    List<LuckyPickPurchaseItemOnly> findAllByDeliveryStatus(Integer status);

    List<LuckyPickPurchaseItemOnly> findAllByLuckyPickPurchaseSeqNo(Long luckyPickPurchaseSeqNo);
}