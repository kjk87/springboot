package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.ShoppingGroup;
import kr.co.pplus.store.api.jpa.repository.ShoppingGroupRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ShoppingGroupService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(ShoppingGroupService.class);



	@Autowired
	private ShoppingGroupRepository shoppingGroupRepository;


	public ShoppingGroup getShoppingGroup(Long seqNo){
		return shoppingGroupRepository.findBySeqNo(seqNo);

	}

}
