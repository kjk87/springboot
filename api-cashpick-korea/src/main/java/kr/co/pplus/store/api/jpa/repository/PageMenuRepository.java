package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface PageMenuRepository extends JpaRepository<PageMenu, Long> {

    Page<PageMenu> findAllByPageSeqNoAndStatus(Long pageSeqNo, Integer status, Pageable pageable);
    Page<PageMenu> findAllByPageSeqNoAndStatusAndBlind(Long pageSeqNo, Integer status, Boolean blind, Pageable pageable);
    PageMenu findBySeqNo(Long seqNo);

    void deleteBySeqNo(Long seqNo);
}