package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MobileBrand;
import kr.co.pplus.store.api.jpa.model.MobileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MobileBrandRepository extends JpaRepository<MobileBrand, Long> {

    List<MobileBrand> findAllByCategorySeqNoAndStatusOrderByArrayDesc(Long categorySeqNo, String status);
}