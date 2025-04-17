package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PointClickReward;
import kr.co.pplus.store.api.jpa.model.TnkRewardJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PointClickRewardRepository extends JpaRepository<PointClickReward, Long> {
    boolean existsByTransactionKey(String transactionKey);

}