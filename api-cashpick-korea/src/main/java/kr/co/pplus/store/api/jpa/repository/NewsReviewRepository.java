package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.NewsReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface NewsReviewRepository extends JpaRepository<NewsReview, Long> {


    Page<NewsReview> findAllByNewsSeqNoAndDeleted(Long newsSeqNo, Boolean deleted, Pageable pageable);
}