package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Prepayment;
import kr.co.pplus.store.api.jpa.model.PrepaymentDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PrepaymentRepository extends JpaRepository<Prepayment, Long> {

    Prepayment findBySeqNo(Long seqNo);

}