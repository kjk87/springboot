package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    @Query(value = "select * from order_menu "
            + " where page_seq_no =:pageSeqNo "
            + " and delegate = true "
            + " and deleted = false "
            + " and is_sold_out = false "
            + " and (sold_out_date != curdate() or sold_out_date is null) ", nativeQuery = true)
    List<OrderMenu> findAllDelegateMenuByPageSeqNo(Long pageSeqNo);

    List<OrderMenu> findAllByPageSeqNoAndGroupSeqNoAndDeletedOrderByDelegateDesc(Long pageSeqNo, Long groupSeqNo, Boolean deleted);

    List<OrderMenu> findAllByPageSeqNoAndDeletedOrderByDelegateDesc(Long pageSeqNo, Boolean deleted);

    @Modifying
    @Query(value = "UPDATE order_menu set delegate = :delegate where seq_no = :seqNo", nativeQuery = true)
    void updateOrderMenuDelegate(Long seqNo, Boolean delegate);

    @Modifying
    @Query(value = "UPDATE order_menu set is_sold_out = :isSoldOut where seq_no = :seqNo", nativeQuery = true)
    void updateOrderMenuSoldOut(Long seqNo, Boolean isSoldOut);

    @Modifying
    @Query(value = "UPDATE order_menu set sold_out_date = :now where seq_no = :seqNo", nativeQuery = true)
    void updateOrderMenuTodaySoldOut(Long seqNo, String now);
}