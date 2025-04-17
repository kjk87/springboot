package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxPurchaseItemCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxPurchaseItemCustomRepository extends JpaRepository<LuckyBoxPurchaseItemCustom, Long> {

    @Query(value = "(SELECT *, (select count(1) from luckybox_reply where luckybox_purchase_item_seq_no = lpi.seq_no and status = 1) as reply_count, true as is_luckybox "
            + " FROM pplus.luckybox_purchase_item lpi "
            + " where lpi.seq_no >= 112 and lpi.is_open = true and lpi.status = 2 and lpi.delivery_status is not null) "
            + " union all "
            + "(SELECT *, (select count(1) from lucky_pick_reply where lucky_pick_purchase_item_seq_no = lpi.seq_no and status = 1) as reply_count, false as is_luckybox "
            + " FROM pplus.lucky_pick_purchase_item lpi "
            + " where lpi.seq_no > 109 and lpi.is_open = true and lpi.status = 2)"
            + " order by open_datetime desc "
            ,
            countQuery = "select count(*) from ((SELECT seq_no FROM pplus.luckybox_purchase_item where seq_no >= 112 and is_open = true and status = 2 and delivery_status is not null)"
                    + " union all (SELECT seq_no FROM pplus.lucky_pick_purchase_item where seq_no > 109 and is_open = true and status = 2)) as lpi"
            , nativeQuery = true)
    Page<LuckyBoxPurchaseItemCustom> findAllByUnion(Pageable pageable);

}