package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PrepaymentDetail;
import kr.co.pplus.store.api.jpa.model.PrepaymentPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PrepaymentDetailRepository extends JpaRepository<PrepaymentDetail, Long> {

    PrepaymentDetail findBySeqNo(Long seqNo);

    Page<PrepaymentDetail> findAllByPageSeqNoOrderBySeqNoDesc(Long pageSeqNo, Pageable pageable);

}