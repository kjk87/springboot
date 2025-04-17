package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboReview;
import kr.co.pplus.store.api.jpa.model.ComboWin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboReviewRepository extends JpaRepository<ComboReview, Long> {

    ComboReview findBySeqNo(Long seqNo);

}