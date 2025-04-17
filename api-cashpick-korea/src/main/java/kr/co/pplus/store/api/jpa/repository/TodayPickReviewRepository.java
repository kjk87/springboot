package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboReview;
import kr.co.pplus.store.api.jpa.model.TodayPickReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickReviewRepository extends JpaRepository<TodayPickReview, Long> {

    TodayPickReview findBySeqNo(Long seqNo);

}