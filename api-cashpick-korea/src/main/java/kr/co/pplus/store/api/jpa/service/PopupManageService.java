package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.PopupManage;
import kr.co.pplus.store.api.jpa.repository.PopupManageRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PopupManageService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PopupManageService.class);



	@Autowired
	private PopupManageRepository popupManageRepository;


	public List<PopupManage> getPopupList(String platform, String appType){
		String dateStr = AppUtil.localDatetimeNowString();
		if(platform.equals("aos")){
			return popupManageRepository.findAllByAndroidAndDisplayAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppType(true, true, dateStr, dateStr, appType);
		}else if(platform.equals("ios")){
			return popupManageRepository.findAllByIosAndDisplayAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppType(true, true, dateStr, dateStr, appType);
		}
		return null;
	}

}
