package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>{

    Purchase findByOrderId(String orderId);

    Purchase findBySeqNo(Long seqNo);

    @Modifying
    @Query(value = "UPDATE purchase set status = :status, mod_datetime = :modDatetime where seq_no = :seqNo", nativeQuery = true)
    void updatePurchaseCompleteBySeqNo(@Param("status") Integer status, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);
}
