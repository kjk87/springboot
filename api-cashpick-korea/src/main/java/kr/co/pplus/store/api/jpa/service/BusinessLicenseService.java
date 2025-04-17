package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.BusinessLicense;
import kr.co.pplus.store.api.jpa.repository.BusinessLicenseRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.api.util.CorporationNumber;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class BusinessLicenseService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(BusinessLicenseService.class);

	@Autowired
	private BusinessLicenseRepository businessLicenseRepository;

	@Transactional(transactionManager = "jpaTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public BusinessLicense insertOrUpdate(BusinessLicense businessLicense) throws ResultCodeException {

		Optional<BusinessLicense> optionalBusinessLicense = businessLicenseRepository.findByPage(businessLicense.getPage());
		if(optionalBusinessLicense.isPresent()){
			businessLicense.setId(optionalBusinessLicense.get().getId());
		}else{
			String dateStr = AppUtil.localDatetimeNowString();
			businessLicense.setId(null);
			businessLicense.setRegDatetime(dateStr);
		}
		businessLicense = businessLicenseRepository.saveAndFlush(businessLicense);
		return businessLicense;
	}

	public BusinessLicense getBusinessLicense(Long pageSeqNo){
		if(businessLicenseRepository.findByPage(pageSeqNo).isPresent()){
			return businessLicenseRepository.findByPage(pageSeqNo).get();
		}

		return null;

	}

	public boolean isValidCorperationNumber(String number){
		CorporationNumber corporationNumber = new CorporationNumber();
		return corporationNumber.getInfo(number);
	}
}
