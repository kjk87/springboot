package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyDetail;
import kr.co.pplus.store.api.jpa.model.BuyRefDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyRefDetailRepository extends JpaRepository<BuyRefDetail, Long> {

    BuyRefDetail findBySeqNo(Long seqNo);


    BuyRefDetail  findByOrderId(String orderId) ;

    BuyRefDetail  findByPgTranId(String pgTranId);

    BuyRefDetail  findByPgAcceptId(String pgAcceptId);

    Page<BuyRefDetail> findAllByMemberSeqNoAndProcessGreaterThan(Long memberSeqNo, Integer process, Pageable pageable);

    Page<BuyRefDetail> findAllByProcessGreaterThan(Integer process, Pageable pageable) ;

}