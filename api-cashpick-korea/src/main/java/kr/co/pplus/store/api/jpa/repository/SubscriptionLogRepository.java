package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.SubscriptionDownload;
import kr.co.pplus.store.api.jpa.model.SubscriptionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface SubscriptionLogRepository extends JpaRepository<SubscriptionLog, Long> {

    Page<SubscriptionLog> findAllByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable);

    List<SubscriptionLog> findAllBySubscriptionSeqNoOrderBySeqNoAsc(Long subscriptionSeqNo);
    List<SubscriptionLog> findAllBySubscriptionSeqNoOrderBySeqNoDesc(Long subscriptionSeqNo);

    Page<SubscriptionLog> findAllBySubscriptionSeqNo(Long subscriptionSeqNo, Pageable pageable);

    Integer countByProductPriceSeqNo(Long productPriceSeqNo);
}