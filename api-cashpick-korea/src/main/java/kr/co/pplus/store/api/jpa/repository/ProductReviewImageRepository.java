package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ProductReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ProductReviewImageRepository extends JpaRepository<ProductReviewImage, Long> {

    @javax.transaction.Transactional
    void deleteAllByProductReviewSeqNo(Long productReviewSeqNo);
}