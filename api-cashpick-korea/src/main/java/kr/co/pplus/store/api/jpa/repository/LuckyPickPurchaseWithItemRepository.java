package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseWithItem;
import kr.co.pplus.store.api.jpa.model.LuckyPickPurchaseWithItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickPurchaseWithItemRepository extends JpaRepository<LuckyPickPurchaseWithItem, Long> {

    @Query(value="select lp.*, (select count(1) from lucky_pick_purchase_item where lucky_pick_purchase_seq_no = lp.seq_no and is_open = true) = 0 as is_cancelable "
            + " from lucky_pick_purchase lp "
            + " where (select count(1) from lucky_pick_purchase_item where lucky_pick_purchase_seq_no = lp.seq_no and is_open = false) > 0 "
            + " and lp.member_seq_no = :memberSeqNo"
            + " and lp.status = 2"
            + " order by lp.seq_no desc", nativeQuery = true)
    List<LuckyPickPurchaseWithItem> findAllBy(@Param("memberSeqNo") Long memberSeqNo);

}