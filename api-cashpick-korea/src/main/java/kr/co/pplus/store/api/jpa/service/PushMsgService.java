package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.PushMsg;
import kr.co.pplus.store.api.jpa.repository.PushMsgRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PushMsgService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PushMsgService.class);

	@Autowired
	PushMsgRepository pushMsgRepository;

	public Page<PushMsg> getPushMsgListByPageSeqNo(Long pageSeqNo, Pageable pageable){
		return pushMsgRepository.findAllByPageSeqNo(pageSeqNo, pageable);
	}

}
