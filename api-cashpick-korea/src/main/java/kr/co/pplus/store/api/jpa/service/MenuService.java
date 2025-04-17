package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidGoodsReviewException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class MenuService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(MenuService.class);

	@Autowired
	OrderMenuGroupRepository orderMenuGroupRepository;

	@Autowired
	OrderMenuGroupWithMenuRepository orderMenuGroupWithMenuRepository;

	@Autowired
	OrderMenuRepository orderMenuRepository;

	@Autowired
	OrderMenuWithOptionRepository orderMenuWithOptionRepository;

	@Autowired
	MenuOptionDetailRepository menuOptionDetailRepository;

	@Autowired
	OrderMenuReviewRepository orderMenuReviewRepository;

	@Autowired
	OrderMenuReviewImageRepository orderMenuReviewImageRepository;

	@Autowired
	PageJpaService pageJpaService;

	public MenuOptionDetail getMenuOptionDetail(Long seqNo){
		return menuOptionDetailRepository.findBySeqNo(seqNo);
	}

	public List<OrderMenuGroup> getOrderMenuGroupList(Long pageSeqNo){
		return orderMenuGroupRepository.findAllByPageSeqNoAndDeletedOrderByArrayAsc(pageSeqNo, false);
	}

	public List<OrderMenuGroupWithMenu> getOrderMenuGroupWithMenuList(Long pageSeqNo){
		return orderMenuGroupWithMenuRepository.findAllByPageSeqNoOrderByArrayAsc(pageSeqNo);
	}

	public List<OrderMenu> getOrderMenuListFromCS(Long pageSeqNo, Long groupSeqNo){
		if(groupSeqNo == null){
			return orderMenuRepository.findAllByPageSeqNoAndDeletedOrderByDelegateDesc(pageSeqNo, false);
		}else{
			return orderMenuRepository.findAllByPageSeqNoAndGroupSeqNoAndDeletedOrderByDelegateDesc(pageSeqNo, groupSeqNo, false);
		}

	}

	public List<OrderMenu> getDelegateOrderMenuList(Long pageSeqNo){
		return orderMenuRepository.findAllDelegateMenuByPageSeqNo(pageSeqNo);
	}

	public OrderMenuWithOption getMenu(Long seqNo){
		return orderMenuWithOptionRepository.findBySeqNo(seqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertReview(OrderMenuReview orderMenuReview) throws ResultCodeException {

		try {

			Set<OrderMenuReviewImage> imageList = orderMenuReview.getImageList();
			String dateStr = AppUtil.localDatetimeNowString();
			orderMenuReview.setSeqNo(null);
			orderMenuReview.setRegDatetime(dateStr);
			orderMenuReview.setModDatetime(dateStr);
			orderMenuReview = orderMenuReviewRepository.saveAndFlush(orderMenuReview);

			if (imageList != null && imageList.size() > 0) {

				for (OrderMenuReviewImage image : imageList) {
					image.setOrderMenuReviewSeqNo(orderMenuReview.getSeqNo());
					orderMenuReviewImageRepository.save(image);
				}
			}
			return Const.E_SUCCESS;
		} catch (Exception e) {
			logger.error(e.toString());
			throw new InvalidGoodsReviewException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateReview(OrderMenuReview orderMenuReview) throws ResultCodeException {

		try {

			if (orderMenuReview.getSeqNo() == null) {
				throw new InvalidGoodsReviewException("/eventReview[PUT]", "eventReview.seq_no cannot be null");
			}

			String dateStr = AppUtil.localDatetimeNowString();

			OrderMenuReview saved = orderMenuReviewRepository.findBySeqNo(orderMenuReview.getSeqNo());
			saved.setEval(orderMenuReview.getEval());
			saved.setReview(orderMenuReview.getReview());
			saved.setModDatetime(dateStr);

			Set<OrderMenuReviewImage> imageList = orderMenuReview.getImageList();


			saved = orderMenuReviewRepository.saveAndFlush(saved);

			orderMenuReviewImageRepository.deleteAllByOrderMenuReviewSeqNo(saved.getSeqNo());

			if (imageList != null && imageList.size() > 0) {

				for (OrderMenuReviewImage image : imageList) {
					image.setOrderMenuReviewSeqNo(saved.getSeqNo());
					orderMenuReviewImageRepository.save(image);
				}
			}
			return Const.E_SUCCESS;
		} catch (Exception e) {
			logger.error(e.toString());
			throw new InvalidGoodsReviewException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int deleteReview(Long seqNo) throws ResultCodeException {

		orderMenuReviewImageRepository.deleteAllByOrderMenuReviewSeqNo(seqNo);
		OrderMenuReview orderMenuReview = new OrderMenuReview();
		orderMenuReview.setSeqNo(seqNo);
		orderMenuReviewRepository.delete(orderMenuReview);
		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int updateReviewReply(User user, OrderMenuReview orderMenuReview) throws ResultCodeException {
		PageDetail page = pageJpaService.getPageByMemberSeqNo(user.getNo());

		if(!page.getSeqNo().equals(orderMenuReview.getPageSeqNo())){
			throw new InvalidGoodsReviewException("orderMenuReview not page owner");
		}

		String dateStr = AppUtil.localDatetimeNowString();

		OrderMenuReview saved = orderMenuReviewRepository.findBySeqNo(orderMenuReview.getSeqNo());
		saved.setReviewReply(orderMenuReview.getReviewReply());
		saved.setModDatetime(dateStr);


		if(StringUtils.isNotEmpty(orderMenuReview.getReviewReply())){
			saved.setReviewReplyDate(dateStr);
		}else{
			saved.setReviewReplyDate(null);
		}
		saved = orderMenuReviewRepository.saveAndFlush(saved);
		return Const.E_SUCCESS;
	}

	public Page<OrderMenuReview> getReviewByMemberSeqNo(User user, Pageable pageable) throws ResultCodeException {
		return orderMenuReviewRepository.findAllByMemberSeqNo(user.getNo(), pageable);
	}

	public Page<OrderMenuReview> getReviewByPageSeqNo(Long pageSeqNo, Pageable pageable) throws ResultCodeException {
		return orderMenuReviewRepository.findAllByPageSeqNo(pageSeqNo, pageable);
	}

	public List<ReviewCountEval> getReviewCountGroupByEvalByPageSeqNo(Long pageSeqNo) {

		List<ReviewCountEval> list = new ArrayList<>();

		for (int i = 5; i > 0; i--) {
			ReviewCountEval reviewCountEval = new ReviewCountEval();
			Integer count = orderMenuReviewRepository.findReviewCountGroupByEvalByPageSeqNo(pageSeqNo, i);
			reviewCountEval.setCount(count);
			reviewCountEval.setEval(i);
			list.add(reviewCountEval);
		}

		return list;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updateOrderMenuDelegate(Long seqNo, Boolean delegate) throws ResultCodeException {
		orderMenuRepository.updateOrderMenuDelegate(seqNo, delegate);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updateOrderMenuSoldOut(Long seqNo, Boolean isSoldOut) throws ResultCodeException {
		orderMenuRepository.updateOrderMenuSoldOut(seqNo, isSoldOut);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updateOrderMenuTodaySoldOut(Long seqNo, Boolean isResume) throws ResultCodeException {
		if(isResume){
			orderMenuRepository.updateOrderMenuTodaySoldOut(seqNo, null);
		}else{
			orderMenuRepository.updateOrderMenuTodaySoldOut(seqNo, AppUtil.localDatetimeNowString());
		}

	}
}
