package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.CashExchange;
import kr.co.pplus.store.api.jpa.repository.CashExchangeRepository;
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
public class CashExchangeService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(CashExchangeService.class);

	@Autowired
	private CashExchangeRepository cashExchangeRepository;


	public Page<CashExchange> getCashExchangeList(Long memberSeqNo, Pageable pageable){
		return cashExchangeRepository.findAllByMemberSeqNoOrderBySeqNoDesc(memberSeqNo, pageable);

	}

	public CashExchange getCashExchange(Long seqNo){
		return cashExchangeRepository.findBySeqNo(seqNo);
	}
}
