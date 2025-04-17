package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Partnership;
import kr.co.pplus.store.api.jpa.repository.PartnerShipRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PartnerShipService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PartnerShipService.class);

	@Autowired
	PartnerShipRepository partnerShipRepository;

	public Partnership getPartnerShip(String code){
		return partnerShipRepository.findByCode(code);
	}

}
