package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CashExchange;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface CashExchangeRepository extends JpaRepository<CashExchange, Long> {


    void deleteBySeqNo(Long seqNo) ;

    CashExchange  findBySeqNo(Long seqNo) ;

    Page<CashExchange>  findAllByMemberSeqNoOrderBySeqNoDesc(Long memberSeqNo, Pageable pageable) ;
}