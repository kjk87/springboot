package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Attachment;
import kr.co.pplus.store.api.jpa.model.BuyGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Attachment findBySeqNo(Long seqNo) ;
    Attachment findById(String id) ;
}