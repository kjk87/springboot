package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.LocationServiceLog;
import kr.co.pplus.store.api.jpa.repository.LocationServiceLogRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class LocationServiceLogService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(LocationServiceLogService.class);



	@Autowired
	LocationServiceLogRepository locationServiceLogRepository;

	public void saveLog(String loginId, String deviceId, String platform, String serviceLog){

		LocationServiceLog locationServiceLog = new LocationServiceLog();
		if(AppUtil.isEmpty(loginId)){
			locationServiceLog.setId(deviceId);
		}else{
			locationServiceLog.setId(loginId);
		}
		locationServiceLog.setPlatform(platform);
		locationServiceLog.setServiceLog(serviceLog);
		locationServiceLog.setRegDatetime(AppUtil.localDatetimeNowString());
		locationServiceLogRepository.save(locationServiceLog);
	}

	public void delete(String loginId){
		locationServiceLogRepository.deleteAllById(loginId);
	}

}
