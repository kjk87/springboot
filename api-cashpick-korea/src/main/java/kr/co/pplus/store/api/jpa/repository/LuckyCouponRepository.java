package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyCoupon;
import kr.co.pplus.store.api.jpa.model.LuckyCouponSend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyCouponRepository extends JpaRepository<LuckyCoupon, Long> {

    @Query(value="update lucky_coupon set status = 3, update_datetime = :datetime where status = 1 and valid_datetime < :datetime", nativeQuery = true)
    void updateExpired(String datetime);

}
