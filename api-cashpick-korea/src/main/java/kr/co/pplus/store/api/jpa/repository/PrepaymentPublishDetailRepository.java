package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PrepaymentPublishDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PrepaymentPublishDetailRepository extends JpaRepository<PrepaymentPublishDetail, Long> {

    PrepaymentPublishDetail findBySeqNo(Long seqNo);

    Page<PrepaymentPublishDetail> findAllByPageSeqNoAndStatusInOrderBySeqNoDesc(Long pageSeqNo, List<String> statusList, Pageable pageable);

}