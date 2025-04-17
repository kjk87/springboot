package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickPurchaseItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickPurchaseItemRepository extends JpaRepository<LuckyPickPurchaseItem, Long> {

    List<LuckyPickPurchaseItem> findAllByMemberSeqNoAndStatusAndIsOpenOrderBySeqNoDesc(Long memberSeqNo, Integer status, Boolean isOpen);

    List<LuckyPickPurchaseItem> findAllByMemberSeqNoAndStatusAndIsOpenAndLuckyPickPurchaseSeqNoOrderBySeqNoDesc(Long memberSeqNo, Integer status, Boolean isOpen, Long luckyPickPurchaseSeqNo);

    Page<LuckyPickPurchaseItem> findAllByMemberSeqNoAndStatusInAndIsOpenOrderByOpenDatetimeDesc(Long memberSeqNo, List<Integer> statusList, Boolean isOpen, Pageable pageable);

    Page<LuckyPickPurchaseItem> findAllByStatusInAndIsOpenOrderBySeqNoDesc(List<Integer> statusList, Boolean isOpen, Pageable pageable);

    Integer countByMemberSeqNoAndStatusAndIsOpen(Long memberSeqNo, Integer status, Boolean isOpen);

    Integer countByLuckyPickDeliveryPurchaseSeqNoAndIsOpen(Long luckyPickPurchaseSeqNo, Boolean isOpen);

    LuckyPickPurchaseItem findBySeqNo(Long seqNo);
}