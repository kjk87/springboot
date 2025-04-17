package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.NewsReviewOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface NewsReviewOnlyRepository extends JpaRepository<NewsReviewOnly, Long> {
    NewsReviewOnly findBySeqNo(Long seqNo);
}