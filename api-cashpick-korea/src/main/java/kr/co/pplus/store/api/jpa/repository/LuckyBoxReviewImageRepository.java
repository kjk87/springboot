package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxReview;
import kr.co.pplus.store.api.jpa.model.LuckyBoxReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxReviewImageRepository extends JpaRepository<LuckyBoxReviewImage, Long> {
    void deleteAllByLuckyBoxReviewSeqNo(Long luckyBoxReviewSeqNo);
}