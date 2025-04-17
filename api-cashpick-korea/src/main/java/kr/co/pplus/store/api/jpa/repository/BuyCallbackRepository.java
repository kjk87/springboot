package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buy;
import kr.co.pplus.store.api.jpa.model.BuyCallback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyCallbackRepository extends JpaRepository<BuyCallback, Long> {

    BuyCallback findBySeqNo(Long seqNo);

    BuyCallback findByOrderId(String orderId) ;

    BuyCallback findByPgTranId(String pgTranId);

    List<BuyCallback> findAllByBuySeqNo(Long buySeqNo);

    Page<BuyCallback> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);


}