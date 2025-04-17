package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderPurchaseDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderPurchaseDetailRepository extends JpaRepository<OrderPurchaseDetail, Long> {

    Page<OrderPurchaseDetail> findAllByMemberSeqNoAndStatusGreaterThanEqualAndSalesTypeIn(Long memberSeqNo, Integer status, List<Long> salesTypeList, Pageable pageable);

    Page<OrderPurchaseDetail> findAllByPageSeqNoAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, List<Integer> statusList, String startDateTime, String endDateTime, Pageable pageable);

    Page<OrderPurchaseDetail> findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, String startDateTime, String endDateTime, Pageable pageable);

    OrderPurchaseDetail findBySeqNo(Long seqNo);

    Long countByPageSeqNoAndStatusAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, Integer status, String startDateTime, String endDateTime);

    Long countByPageSeqNoAndStatusAndAppTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, Integer status, String appType, String startDateTime, String endDateTime);

    Long countByPageSeqNoAndStatusAndSalesTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, Integer status, Long salesType, String startDateTime, String endDateTime);

    @Query(value = "select ifnull(sum(price), 0) as price from order_purchase "
            + " where page_seq_no = :pageSeqNo "
            + " and status = :status"
            + " and reg_datetime >= :startDateTime "
            + " and reg_datetime <= :endDateTime ",
            nativeQuery = true)
    Float sumPrice(Long pageSeqNo, Integer status, String startDateTime, String endDateTime);

    @Query(value = "select ifnull(avg(price), 0) as price from order_purchase "
            + " where page_seq_no = :pageSeqNo "
            + " and status = :status"
            + " and reg_datetime >= :startDateTime "
            + " and reg_datetime <= :endDateTime ",
            nativeQuery = true)
    Float avgPrice(Long pageSeqNo, Integer status, String startDateTime, String endDateTime);


}