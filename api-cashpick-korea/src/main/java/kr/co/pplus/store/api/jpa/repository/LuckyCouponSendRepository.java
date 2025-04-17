package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyCouponSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyCouponSendRepository extends JpaRepository<LuckyCouponSend, Long> {
    List<LuckyCouponSend> findAllByStatusAndSendDateTimeLessThanEqual(String status, String sendDatetime);

}
