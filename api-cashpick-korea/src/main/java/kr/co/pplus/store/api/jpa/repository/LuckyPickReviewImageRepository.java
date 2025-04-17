package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyPickReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickReviewImageRepository extends JpaRepository<LuckyPickReviewImage, Long> {
    void deleteAllByLuckyPickReviewSeqNo(Long luckyPickReviewSeqNo);
}