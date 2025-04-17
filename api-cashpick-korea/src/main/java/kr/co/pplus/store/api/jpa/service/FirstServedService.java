package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.FirstServed;
import kr.co.pplus.store.api.jpa.repository.FirstServedRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class FirstServedService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(FirstServedService.class);



	@Autowired
	private FirstServedRepository firstServedRepository;


	public FirstServed getFirstServed(){

		List<String> statusList = new ArrayList<>();
		statusList.add("active");

		String dateStr = AppUtil.localDatetimeNowString();
		return firstServedRepository.findFirstByStatusInAndAosAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderBySeqNoDesc(statusList, true, dateStr, dateStr);
	}

}
