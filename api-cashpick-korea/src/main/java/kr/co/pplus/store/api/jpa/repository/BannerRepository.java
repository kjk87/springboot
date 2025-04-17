package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Banner;
import kr.co.pplus.store.api.jpa.model.PopupManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findAllByAndroidAndDisplayAndTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppTypeOrderByAndroidArrayAsc(Boolean android, Boolean display, String type, String startDatetime, String endDatetime, String appType);
    List<Banner> findAllByIosAndDisplayAndTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppTypeOrderByIosArrayAsc(Boolean ios, Boolean display, String type, String startDatetime, String endDatetime, String appType);


}