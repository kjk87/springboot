package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PurchaseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PurchaseProductRepository extends JpaRepository<PurchaseProduct, Long> {

    List<PurchaseProduct> findAllByPurchaseSeqNo(Long purchaseSeqNo);

    List<PurchaseProduct> findAllByReserveStatusAndEndDateLessThan(@Param("reserveStatus") Integer reserveStatus, @Param("modDatetime") String modDatetime);

    List<PurchaseProduct> findAllByReserveStatusAndSalesTypeAndIsPaymentPointAndRegDatetimeLessThan(@Param("reserveStatus") Integer reserveStatus, @Param("salesType") Long salesType, @Param("isPaymentPoint") Boolean isPaymentPoint, @Param("currentDatetime") String currentDatetime);


    PurchaseProduct findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "UPDATE purchase_product set purchase_delivery_seq_no = :purchaseDeliverySeqNo where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseDeliverySeqNoBySeqNo(@Param("seqNo") Long seqNo, @Param("purchaseDeliverySeqNo") Long purchaseDeliverySeqNo);

    @Modifying
    @Query(value = "UPDATE purchase_product set status = 13, cancel_datetime = :modDatetime, change_status_datetime = :modDatetime where purchase_seq_no = :purchaseSeqNo", nativeQuery = true)
    void updateCancelByPurchaseSeqNo(@Param("purchaseSeqNo") Long purchaseSeqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query(value = "UPDATE purchase_product set reserve_status = 3, change_status_datetime = :modDatetime where reserve_status = :reserveStatus and end_date < :modDatetime", nativeQuery = true)
    void updatePurchaseProductExpired(@Param("reserveStatus") Integer reserveStatus, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query(value = "UPDATE purchase_product set reserve_status = 3, change_status_datetime = CURRENT_TIMESTAMP where reserve_status = 1 and ((reg_datetime <= CURRENT_TIMESTAMP and end_time <= DATE_FORMAT(CURRENT_TIMESTAMP, '%H:%i')) or end_date < CURRENT_TIMESTAMP)", nativeQuery = true)
    void updatePurchaseProductExpiredTicket();

    @Query(value = "select * from purchase_product  where reserve_status = 1 and ((reg_datetime <= CURRENT_TIMESTAMP and end_time <= DATE_FORMAT(CURRENT_TIMESTAMP, '%H:%i')) or end_date < CURRENT_TIMESTAMP)", nativeQuery = true)
    List<PurchaseProduct> findAllPurchaseProductExpiredTicket();

    @Modifying
    @Query(value = "UPDATE purchase_product set is_payment_point=:isPaymentPoint where seq_no = :seqNo", nativeQuery = true)
    void updatePaymentPointBySeqNo(@Param("seqNo") Long seqNo, @Param("isPaymentPoint") Integer isPaymentPoint);

    @Modifying
    @Query(value = "UPDATE purchase_product set is_payment_bol=:isPaymentBol where seq_no = :seqNo", nativeQuery = true)
    void updatePaymentBolBySeqNo(@Param("seqNo") Long seqNo, @Param("isPaymentBol") Integer isPaymentBol);

    @Modifying
    @Query(value = "UPDATE purchase_product set status = :status, is_status_completed = true, delivery_status = :deliveryStatus, change_status_datetime = :modDatetime where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseProductDeliveryStatusCompleteBySeqNo(@Param("status") Integer status, @Param("deliveryStatus") Integer deliveryStatus, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query(value = "UPDATE purchase_product set status = :status, is_status_completed = true, reserve_status = :reserveStatus, change_status_datetime = :modDatetime where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseProductReserveStatusCompleteBySeqNo(@Param("status") Integer status, @Param("reserveStatus") Integer reserveStatus, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query(value = "UPDATE purchase_product set status = :status, delivery_status = :deliveryStatus, change_status_datetime = :modDatetime, pay_datetime = :modDatetime where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseProductPayStatusBySeqNo(@Param("status") Integer status, @Param("deliveryStatus") Integer deliveryStatus, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);

    @Modifying
    @Query(value = "UPDATE purchase_product set delivery_status = :deliveryStatus, change_status_datetime = :modDatetime where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseProductDeliveryStatusBySeqNo(@Param("deliveryStatus") Integer deliveryStatus, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);


    @Query(value = "select ifnull(sum(price), 0) as price from purchase_product "
            + " where 1=1 "
            + " and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:supplyPageSeqNo) = 1 or supply_page_seq_no = :supplyPageSeqNo ) "
            + " and ( status = 2 or status = 99)"
            + " and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            + " and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float sumSalePrice(@Param("pageSeqNo") Long pageSeqNo,@Param("supplyPageSeqNo") Long supplyPageSeqNo,
                               @Param("startDuration") String startDuration,
                               @Param("endDuration") String endDuration);


    @Query(value = "select count(1) from purchase_product "
            + " where 1=1 "
            + " and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            + " and ( isnull(:supplyPageSeqNo) = 1 or supply_page_seq_no = :supplyPageSeqNo ) "
            + " and ( isnull(:productType) = 1 or ticket_product_type = :productType ) "
            + " and ( status = 2 or status = 99)"
            + " and reg_datetime >= :startDuration "
            + " and reg_datetime <= :endDuration ",
            nativeQuery = true)
    Integer saleCount(@Param("pageSeqNo") Long pageSeqNo,@Param("supplyPageSeqNo") Long supplyPageSeqNo,
                       @Param("startDuration") String startDuration,
                       @Param("endDuration") String endDuration,
                       @Param("productType") String productType);

    @Query(value = "select * from purchase_product pp "
            + " inner join purchase_delivery pd on pd.seq_no = pp.purchase_delivery_seq_no "
            + " where status = 2 "
            + " and delivery_status = 4 "
            + " and pd.delivery_complete_datetime < date_add(now(), INTERVAL -7 DAY)", nativeQuery = true)
    List<PurchaseProduct> findAllNeedCompleteList();

    List<PurchaseProduct> findAllByStatusAndDeliveryStatus(Integer status, Integer deliveryStatus);
}
