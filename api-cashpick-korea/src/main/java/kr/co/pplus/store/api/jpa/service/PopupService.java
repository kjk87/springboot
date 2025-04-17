package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.Popup;
import kr.co.pplus.store.api.jpa.repository.PopupRepository;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PopupService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PopupService.class);

	@Autowired
	PopupRepository popupRepository;

	public List<Popup> getPopupList(){
		return popupRepository.findAll();
	}

}
