package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface EventReviewImageRepository extends JpaRepository<EventReviewImage, Long> {

    @javax.transaction.Transactional
    void deleteAllByEventReviewSeqNo(Long eventReviewSeqNo);
}