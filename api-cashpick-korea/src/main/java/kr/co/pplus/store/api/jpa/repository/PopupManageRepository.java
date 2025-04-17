package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventJpa;
import kr.co.pplus.store.api.jpa.model.PopupManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PopupManageRepository extends JpaRepository<PopupManage, Long> {

    List<PopupManage> findAllByAndroidAndDisplayAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppType(Boolean android, Boolean display, String startDatetime, String endDatetime, String appType);
    List<PopupManage> findAllByIosAndDisplayAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppType(Boolean ios, Boolean display, String startDatetime, String endDatetime, String appType);


}