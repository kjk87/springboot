package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.repository.CpeReportRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class CpeReportService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(CpeReportService.class);

	@Autowired
	CpeReportRepository cpeReportRepository;


	public Integer countCpeReport(String type, String startDuration, String endDuration){
		return cpeReportRepository.countByTypeAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(type, startDuration, endDuration);
	}
}
