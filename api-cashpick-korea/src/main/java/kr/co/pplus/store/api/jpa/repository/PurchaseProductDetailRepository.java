package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PurchaseProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;


public interface PurchaseProductDetailRepository extends JpaRepository<PurchaseProductDetail, Long>{

    Integer countByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqual(Long memberSeqNo, Long salesType, Integer status);

    Page<PurchaseProductDetail> findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqual(Long memberSeqNo, Long salesType, Integer status, Pageable pageable);
    Page<PurchaseProductDetail> findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqualAndLuckyBolPurchaseSeqNoIsNotNull(Long memberSeqNo, Long salesType, Integer status, Pageable pageable);
    Page<PurchaseProductDetail> findAllByMemberSeqNoAndSalesTypeAndStatusIsGreaterThanEqualAndLuckyBolPurchaseSeqNoIsNull(Long memberSeqNo, Long salesType, Integer status, Pageable pageable);
    Page<PurchaseProductDetail> findAllByProductPriceCodeAndSalesTypeAndStatusIsGreaterThanEqual(String productPriceCode, Long salesType, Integer status, Pageable pageable);
    Page<PurchaseProductDetail> findAllBySupplyPageSeqNoAndSalesTypeAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long supplyPageSeqNo, Long salesType, List<Integer> statusList, String startDuration, String endDuration, Pageable pageable);
    Page<PurchaseProductDetail> findAllBySupplyPageSeqNoAndSalesTypeAndStatusNotInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long supplyPageSeqNo, Long salesType, List<Integer> statusList, String startDuration, String endDuration, Pageable pageable);
    Page<PurchaseProductDetail> findAllByPageSeqNoAndSalesTypeAndStatusInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, Long salesType, List<Integer> statusList, String startDuration, String endDuration, Pageable pageable);
    Page<PurchaseProductDetail> findAllByPageSeqNoAndSalesTypeAndStatusNotInAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(Long pageSeqNo, Long salesType, List<Integer> statusList, String startDuration, String endDuration, Pageable pageable);

    PurchaseProductDetail findBySeqNo(Long seqNo);
}
