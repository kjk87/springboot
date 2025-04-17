package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CashBuy;
import kr.co.pplus.store.api.jpa.model.PointBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface PointBuyRepository extends JpaRepository<PointBuy, Long> {

    PointBuy findByReceiptId(String receiptId);

    PointBuy findBySeqNo(Long seqNo);
}