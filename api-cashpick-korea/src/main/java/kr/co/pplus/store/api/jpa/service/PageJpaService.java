package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PageJpaService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PageJpaService.class);



	@Autowired
	PageDetailRepository pageDetailRepository;

	@Autowired
	PageWithAvgEvalRepository pageWithAvgEvalRepository;

	@Autowired
	ProductService productService;

	@Autowired
	PageBalanceRepository pageBalanceRepository;

	@Autowired
	PageWithPrepaymentRepository pageWithPrePaymentRepository;

	@Autowired
	PrepaymentPublishRepository prepaymentPublishRepository;

	public Page<PageBalance> getPageBalanceListByPageSeqNo(Long pageSeqNo, String startDate, String endDate, Pageable pageable){
		return pageBalanceRepository.findAllByPageSeqNoAndIssueDateGreaterThanEqualAndIssueDateLessThanEqual(pageSeqNo, startDate, endDate, pageable);
	}

	public Integer getPageBalanceTotalPriceByPageSeqNo(Long pageSeqNo, String startDate, String endDate){
		return pageBalanceRepository.sumPrice(pageSeqNo, startDate, endDate);
	}

	public Page<PageDetail> getPageListWithProductPrice(User user, HttpServletRequest request, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {

        Map<String, String> sortMap = new HashMap<String, String>();
	    pageable = this.nativePageable(request, pageable, sortMap);

	    Long memberSeqNo = null;
	    if(user != null){
	    	memberSeqNo = user.getNo();
		}

		Page<PageDetail> page = pageDetailRepository.findAllByLocation(latitude, longitude, memberSeqNo, "offline", pageable);

		for(int i = 0; i < page.getContent().size(); i++){

			Pageable newsPageable = new Pageable() {
				@Override
				public int getPageNumber() {
					return 0;
				}

				@Override
				public int getPageSize() {
					return 5;
				}

				@Override
				public long getOffset() {
					return 0;
				}

				@Override
				public Sort getSort() {
					return Sort.by(Sort.Direction.DESC, "seq_no") ;
				}

				@Override
				public Pageable next() {
					return this;
				}

				@Override
				public Pageable previousOrFirst() {
					return this;
				}

				@Override
				public Pageable first() {
					return this;
				}

				@Override
				public boolean hasPrevious() {
					return false;
				}
			};
			Page<ProductPrice> productPricePage = productService.getProductPriceListStoreTypeByPageSeqNoAndDiscountOnlyNormal(page.getContent().get(i).getSeqNo(), memberSeqNo, null, null, null, newsPageable);
			page.getContent().get(i).setProductPriceList(productPricePage.getContent());
			page.getContent().get(i).setTotalProductPriceElements(productPricePage.getTotalElements());
		}

		return page;
	}

	public Page<PageDetail> getPageListWithSubscription(User user, HttpServletRequest request, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageDetail> page = pageDetailRepository.findAllByLocationExistSubscription(latitude, longitude, memberSeqNo, pageable);

		for(int i = 0; i < page.getContent().size(); i++){

			Pageable newsPageable = new Pageable() {
				@Override
				public int getPageNumber() {
					return 0;
				}

				@Override
				public int getPageSize() {
					return 5;
				}

				@Override
				public long getOffset() {
					return 0;
				}

				@Override
				public Sort getSort() {
					return Sort.by(Sort.Direction.DESC, "seq_no") ;
				}

				@Override
				public Pageable next() {
					return this;
				}

				@Override
				public Pageable previousOrFirst() {
					return this;
				}

				@Override
				public Pageable first() {
					return this;
				}

				@Override
				public boolean hasPrevious() {
					return false;
				}
			};
			Page<ProductPrice> productPricePage = productService.getProductPriceListByIsSubscriptionAndIsPrepaymentOnlyNormal(page.getContent().get(i).getSeqNo(), memberSeqNo, newsPageable);
			page.getContent().get(i).setProductPriceList(productPricePage.getContent());
			page.getContent().get(i).setTotalProductPriceElements(productPricePage.getTotalElements());
		}

		return page;
	}

	public PageDetail getPageByMemberSeqNo(Long memberSeqNo){
		return pageDetailRepository.findByMemberSeqNo(memberSeqNo);
	}

	public PageWithAvgEval getPageBySeqNo(User user, Long seqNo, Double latitude, Double longitude){

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		return pageWithAvgEvalRepository.findBySeqNoWithDistance(seqNo, latitude, longitude, memberSeqNo);
	}

	public Page<PageWithAvgEval> getDeliveryPageList(User user, HttpServletRequest request, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllDeliveryPage(latitude, longitude, memberSeqNo, categoryMajorSeqNo, categoryMinorSeqNo, pageable);


		return page;
	}

	public Page<PageWithAvgEval> getDeliveryPageListByKeyword(User user, HttpServletRequest request, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, String keyword, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllDeliveryPageByKeyword(latitude, longitude, memberSeqNo, categoryMajorSeqNo, categoryMinorSeqNo, keyword, pageable);


		return page;
	}

	public Page<PageWithAvgEval> getVisitPageList(User user, HttpServletRequest request, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllVisitPage(latitude, longitude, memberSeqNo, categoryMajorSeqNo, categoryMinorSeqNo, pageable);


		return page;
	}

	public Page<PageWithAvgEval> getVisitPageListByKeyword(User user, HttpServletRequest request, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, String keyword, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllVisitPageByKeyword(latitude, longitude, memberSeqNo, categoryMajorSeqNo, categoryMinorSeqNo, keyword, pageable);


		return page;
	}

	public Page<PageWithAvgEval> getVisitPageListByArea(User user, HttpServletRequest request, Double latitude, Double longitude, Double top, Double bottom, Double left, Double right, Long categoryMajorSeqNo, Long categoryMinorSeqNo, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);

		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}

		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllVisitPageByArea(latitude, longitude, top, bottom, left, right, memberSeqNo, categoryMajorSeqNo, categoryMinorSeqNo, pageable);


		return page;
	}

	public Page<PageWithAvgEval> getServicePageList(User user, HttpServletRequest request, Double latitude, Double longitude, Long categoryMajorSeqNo, Long categoryMinorSeqNo, Pageable pageable) throws ResultCodeException {

		Map<String, String> sortMap = new HashMap<String, String>();
		pageable = this.nativePageable(request, pageable, sortMap);


		Page<PageWithAvgEval> page = pageWithAvgEvalRepository.findAllServicePage(latitude, longitude, categoryMajorSeqNo, categoryMinorSeqNo, pageable);


		return page;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updateOrderable(Long pageSeqNo, Boolean orderable) {
		pageDetailRepository.updateOrderable(pageSeqNo, orderable);

	}

	public Page<PageWithPrepayment> getPageListWithPrepayment(User user, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {
		Long memberSeqNo = null;
		if(user != null){
			memberSeqNo = user.getNo();
		}
		return pageWithPrePaymentRepository.findAllWithPrepayment(latitude, longitude, memberSeqNo, pageable);
	}

	public Page<PageWithPrepayment> getPageListWithPrepaymentExistVisitLog(User user, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {
		return pageWithPrePaymentRepository.findAllWithPrepaymentExistVisitLog(latitude, longitude, user.getNo(), pageable);
	}

	public Page<PageDetail> getPageListWithPageWithPrepaymentPublish(User user, Double latitude, Double longitude, Pageable pageable) throws ResultCodeException {
		Page<PageDetail> page = pageDetailRepository.findAllWithPrepaymentPublish(latitude, longitude, user.getNo(), pageable);
		for(int i = 0; i < page.getContent().size(); i++){
			List<PrepaymentPublish> prepaymentPublishList = prepaymentPublishRepository.findAllByPageSeqNoAndMemberSeqNoAndStatusIn(page.getContent().get(i).getSeqNo(), user.getNo());
			page.getContent().get(i).setPrepaymentPublishList(prepaymentPublishList);
		}
		return page;
	}

}
