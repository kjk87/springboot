package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.SubscriptionDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface SubscriptionDownloadRepository extends JpaRepository<SubscriptionDownload, Long> {

    Page<SubscriptionDownload> findAllByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable);
    Page<SubscriptionDownload> findAllByMemberSeqNoOrderByStatusAscSeqNoDesc(Long memberSeqNo, Pageable pageable);

    Integer countByProductPriceSeqNo(Long productPriceSeqNo);

    Integer countByMemberSeqNoAndStatus(Long memberSeqNo, Integer status);

    Integer countByMemberSeqNoAndProductPriceSeqNoAndStatus(Long memberSeqNo, Long productPriceSeqNo, Integer status);

    SubscriptionDownload findBySeqNo(Long seqNo);
}