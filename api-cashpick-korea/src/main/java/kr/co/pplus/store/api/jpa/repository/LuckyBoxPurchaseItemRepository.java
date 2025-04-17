package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchase;
import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxPurchaseItemRepository extends JpaRepository<LuckyBoxPurchaseItem, Long> {

    List<LuckyBoxPurchaseItem> findAllByMemberSeqNoAndStatusAndIsOpenOrderBySeqNoDesc(Long memberSeqNo, Integer status, Boolean isOpen);

    List<LuckyBoxPurchaseItem> findAllByMemberSeqNoAndStatusAndIsOpenAndLuckyBoxPurchaseSeqNoOrderBySeqNoDesc(Long memberSeqNo, Integer status, Boolean isOpen, Long luckyBoxPurchaseSeqNo);

    Page<LuckyBoxPurchaseItem> findAllByMemberSeqNoAndStatusInAndIsOpenOrderByOpenDatetimeDesc(Long memberSeqNo, List<Integer> statusList, Boolean isOpen, Pageable pageable);

    Page<LuckyBoxPurchaseItem> findAllByStatusInAndIsOpenAndSeqNoGreaterThanEqualOrderBySeqNoDesc(List<Integer> statusList, Boolean isOpen, Long seqNo, Pageable pageable);
    Page<LuckyBoxPurchaseItem> findAllByStatusInAndIsOpenAndSeqNoGreaterThanEqualAndDeliveryStatusIsNotNullOrderBySeqNoDesc(List<Integer> statusList, Boolean isOpen, Long seqNo, Pageable pageable);


    Integer countByMemberSeqNoAndStatusAndIsOpen(Long memberSeqNo, Integer status, Boolean isOpen);

    Integer countByLuckyBoxDeliveryPurchaseSeqNoAndIsOpen(Long luckyBoxPurchaseSeqNo, Boolean isOpen);

    LuckyBoxPurchaseItem findBySeqNo(Long seqNo);
}