package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolPurchaseRepository extends JpaRepository<LuckyBolPurchase, Long> {

    @Query(value="select group_concat(win_number SEPARATOR '') from lucky_bol_purchase"
            +" where lucky_bol_seq_no = :luckyBolSeqNo and status='active'",
            nativeQuery = true)
    String findWinNumber(Long luckyBolSeqNo);

    LuckyBolPurchase findBySeqNo(Long seqNo);
    LuckyBolPurchase findByPurchaseSeqNo(Long purchaseSeqNo);
    LuckyBolPurchase findByOrderId(String orderId);

    Page<LuckyBolPurchase> findAllByMemberSeqNoAndLuckyBolSeqNoAndStatusOrderBySeqNoDesc(Long memberSeqNo, Long luckyBolSeqNo, String status, Pageable pageable);
    Page<LuckyBolPurchase> findAllByMemberSeqNoAndStatusOrderBySeqNoDesc(Long memberSeqNo, String status, Pageable pageable);

    List<LuckyBolPurchase> findAllByMemberSeqNoAndStatusAndEngageTypeAndPurchaseSeqNoIsNullOrderBySeqNoDesc(Long memberSeqNo, String status, String engageType);

    @Modifying
    @Query(value = "update lucky_bol_purchase set purchase_seq_no = :purchaseSeqNo, mod_datetime = now() where seq_no = :seqNo ", nativeQuery = true)
    void updatePurchaseSeqNo(@Param("seqNo") Long seqNo, @Param("purchaseSeqNo") Long purchaseSeqNo);
}