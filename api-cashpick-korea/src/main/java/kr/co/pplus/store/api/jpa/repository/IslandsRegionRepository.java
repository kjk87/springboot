package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.IslandsRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface IslandsRegionRepository extends JpaRepository<IslandsRegion, Long> {

    Boolean existsByPostcode(String postcode);

    IslandsRegion findByPostcode(String postcode);
}