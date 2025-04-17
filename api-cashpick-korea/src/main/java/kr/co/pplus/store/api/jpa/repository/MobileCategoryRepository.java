package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MemberAttendance;
import kr.co.pplus.store.api.jpa.model.MobileCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MobileCategoryRepository extends JpaRepository<MobileCategory, Long> {

    List<MobileCategory> findAllByStatusOrderByArrayDesc(String status);
}