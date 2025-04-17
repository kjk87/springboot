package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LpngCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LpngCallbackRepository extends JpaRepository<LpngCallback, Long> {

    LpngCallback findBySeqNo(Long seqNo);

    LpngCallback findByOrderId(String orderId) ;

    LpngCallback findByPgTranId(String pgTranId);

    LpngCallback findAllByBuySeqNo(Long buySeqNo);

    LpngCallback findByPurchaseSeqNo(Long purchaseSeqNo);

    LpngCallback findByOrderPurchaseSeqNo(Long orderPurchaseSeqNo);

    LpngCallback findAllByPurchaseSeqNo(Long purchaseSeqNo);

    LpngCallback findAllByLpngOrderNo(String orderNo);

    LpngCallback findByPointBuySeqNo(Long pointBuySeqNo) ;

    Page<LpngCallback> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);


}