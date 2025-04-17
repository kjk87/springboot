package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Banner;
import kr.co.pplus.store.api.jpa.repository.BannerRepository;
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
public class BannerService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(BannerService.class);



	@Autowired
	private BannerRepository bannerRepository;


	public List<Banner> getBannerList(String platform, String type, String appType){
		String dateStr = AppUtil.localDatetimeNowString();
		if(platform.equals("aos")){
			return bannerRepository.findAllByAndroidAndDisplayAndTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppTypeOrderByAndroidArrayAsc(true, true, type, dateStr, dateStr, appType);
		}else if(platform.equals("ios")){
			return bannerRepository.findAllByIosAndDisplayAndTypeAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualAndAppTypeOrderByIosArrayAsc(true, true, type, dateStr, dateStr, appType);
		}
		return null;
	}

}
