package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.PageMenu;
import kr.co.pplus.store.api.jpa.repository.PageMenuRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.NotFoundException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PageMenuService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PageMenuService.class);

	@Autowired
	PageMenuRepository pageMenuRepository;

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public PageMenu regPageMenu(PageMenu pageMenu) throws ResultCodeException {
		String dateStr = AppUtil.localDatetimeNowString();


		Float discountRatio = 100 - (pageMenu.getPrice().floatValue() / pageMenu.getOriginPrice().floatValue() * 100);
		pageMenu.setDiscountRatio(discountRatio);
		pageMenu.setStatus(1);
		pageMenu.setRegDatetime(dateStr);
		pageMenu.setModDatetime(dateStr);

		pageMenu = pageMenuRepository.saveAndFlush(pageMenu);

		return pageMenu;

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public PageMenu updatePageMenu(PageMenu pageMenu) throws ResultCodeException {
		PageMenu saved = pageMenuRepository.findBySeqNo(pageMenu.getSeqNo());

		if(saved == null){
			throw new NotFoundException();
		}

		String dateStr = AppUtil.localDatetimeNowString();
		Float discountRatio = 100 - (pageMenu.getPrice().floatValue() / pageMenu.getOriginPrice().floatValue() * 100);
		pageMenu.setDiscountRatio(discountRatio);
		pageMenu.setModDatetime(dateStr);

		pageMenu.setRegDatetime(saved.getRegDatetime());
		pageMenu.setStatus(saved.getStatus());
		pageMenu.setBlind(saved.getBlind());
		pageMenu.setReason(saved.getReason());

		pageMenu = pageMenuRepository.saveAndFlush(pageMenu);

		return pageMenu;

	}

	public Page<PageMenu> getPageMenuListByPageSeqNo(Long pageSeqNo, Pageable pageable){
		return pageMenuRepository.findAllByPageSeqNoAndStatus(pageSeqNo, 1, pageable);
	}

	public int deletePageMenu(Long seqNo){
		pageMenuRepository.deleteBySeqNo(seqNo);
		return Const.E_SUCCESS;
	}
}
