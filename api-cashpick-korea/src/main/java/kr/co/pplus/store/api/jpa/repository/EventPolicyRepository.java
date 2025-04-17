package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventBuy;
import kr.co.pplus.store.api.jpa.model.EventPolicy;
import kr.co.pplus.store.api.jpa.model.PointBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface EventPolicyRepository extends JpaRepository<EventPolicy, Long> {

    List<EventPolicy> findAllByPageSeqNoAndEventSeqNo(Long pageSeqNo, Long eventSeqNo);
}