package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Supporter;
import kr.co.pplus.store.api.jpa.repository.SupporterRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class SupporterService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(SupporterService.class);



	@Autowired
	private SupporterRepository supporterRepository;

	public Supporter getSupporter(Long memberSeqNo){
		return supporterRepository.findFirstByMemberSeqNo(memberSeqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Supporter saveSupporter(Supporter supporter){
		Supporter saved = supporterRepository.findFirstByMemberSeqNo(supporter.getMemberSeqNo());
		String dateStr = AppUtil.localDatetimeNowString();
		if(saved != null){
			supporter.setSeqNo(saved.getSeqNo());
			supporter.setReason(saved.getReason());
			supporter.setRegDatetime(saved.getRegDatetime());
		}else{
			supporter.setRegDatetime(dateStr);
		}
		supporter.setStatusDatetime(dateStr);

		supporter = supporterRepository.save(supporter);

		return supporter;

	}

}
