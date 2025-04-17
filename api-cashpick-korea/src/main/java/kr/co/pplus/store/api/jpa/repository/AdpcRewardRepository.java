package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.AdpcRewardJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface AdpcRewardRepository extends JpaRepository<AdpcRewardJpa, Long> {


}