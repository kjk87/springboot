package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderMenuReviewImage;
import kr.co.pplus.store.api.jpa.model.ProductReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderMenuReviewImageRepository extends JpaRepository<OrderMenuReviewImage, Long> {

    @javax.transaction.Transactional
    void deleteAllByOrderMenuReviewSeqNo(Long orderMenuReviewSeqNo);
}