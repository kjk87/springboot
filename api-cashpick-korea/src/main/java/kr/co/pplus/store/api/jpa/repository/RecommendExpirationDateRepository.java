package kr.co.pplus.store.api.jpa.repository;


import kr.co.pplus.store.api.jpa.model.RecommendExpirationDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface RecommendExpirationDateRepository extends JpaRepository<RecommendExpirationDate, Long> {

    RecommendExpirationDate findByType(String type) ;
}
