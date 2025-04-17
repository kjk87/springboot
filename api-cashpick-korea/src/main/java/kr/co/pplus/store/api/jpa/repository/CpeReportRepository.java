package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.CpeReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CpeReportRepository extends JpaRepository<CpeReport, Long>{

	Integer countByTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(String type, String startDuration, String endDuration);

}
