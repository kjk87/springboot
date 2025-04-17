package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderPurchaseMenu;
import kr.co.pplus.store.api.jpa.model.OrderPurchaseMenuWithCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderPurchaseMenuWithCountRepository extends JpaRepository<OrderPurchaseMenuWithCount, Long> {

    @Query(value = "select opm.*, count(order_menu_seq_no) as menu_count from order_purchase_menu opm"
            + " inner join order_purchase op on op.seq_no = opm.order_purchase_seq_no "
            + " where op.page_seq_no = :pageSeqNo "
            + " and op.status = :status "
            + " and reg_datetime >= :startDuration "
            + " and reg_datetime <= :endDuration "
            + " group by order_menu_seq_no "
            + " order by menu_count desc "
            + " limit 5",
            nativeQuery = true)
    List<OrderPurchaseMenuWithCount> findAllOrderPurchaseGroupByMenu(Long pageSeqNo, Integer status, String startDuration, String endDuration);
}