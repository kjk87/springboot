package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.VirtualNumberManage;
import kr.co.pplus.store.api.jpa.repository.VirtualNumberManageRepository;
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
public class VirtualNumberService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(VirtualNumberService.class);



	@Autowired
	VirtualNumberManageRepository virtualNumberManageRepository;


	public VirtualNumberManage getVirtualNumberManage(String virtualNumber){

		String dateStr = AppUtil.localDatetimeNowString() ;
		return virtualNumberManageRepository.findByVirtualNumberAndStatusAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(virtualNumber, "normal", dateStr, dateStr);
	}

	public List<VirtualNumberManage> getNbookVirtualNumberManageList(){

		String dateStr = AppUtil.localDatetimeNowString() ;
		return virtualNumberManageRepository.findAllByNbookAndStatusAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqual(true, "normal", dateStr, dateStr);
	}
}
