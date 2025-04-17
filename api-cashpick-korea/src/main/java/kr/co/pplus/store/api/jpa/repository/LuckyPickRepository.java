package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxProductGroupItem;
import kr.co.pplus.store.api.jpa.model.LuckyPick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyPickRepository extends JpaRepository<LuckyPick, Long> {

    Page<LuckyPick> findAllByStatusAndAndroidAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderByArrayAsc(String status, Boolean android, String startDatetime, String endDatetime, Pageable pageable);
}