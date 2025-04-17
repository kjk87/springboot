package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.BuyGoods;
import kr.co.pplus.store.api.jpa.model.GiftStatus;
import kr.co.pplus.store.api.jpa.model.GoodsReview;
import kr.co.pplus.store.api.jpa.model.GoodsReviewDetail;
import kr.co.pplus.store.api.jpa.repository.BuyGoodsRepository;
import kr.co.pplus.store.api.jpa.repository.GoodsReviewDetailRepository;
import kr.co.pplus.store.api.jpa.repository.GoodsReviewRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidGoodsReviewException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.CashBolService;
import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.model.BolHistory;
import kr.co.pplus.store.type.model.EventWin;
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

import java.util.HashMap;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class GoodsReviewService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(GoodsReviewService.class);


	@Autowired
	GoodsReviewRepository goodsReviewRepository;

	@Autowired
	BuyGoodsRepository buyGoodsRepository;

	@Autowired
	GoodsReviewDetailRepository goodsReviewDetailRepository;

	@Autowired
	PageService pageService;

	@Autowired
	CashBolService cashBolService;

	@Autowired
	EventService eventService;

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public GoodsReview addGoodsReview(User user, GoodsReview goodsReview) throws ResultCodeException {

		try {

			String dateStr = AppUtil.localDatetimeNowString();
			goodsReview.setSeqNo(null);
			goodsReview.setRegDatetime(dateStr);
			goodsReview.setModDatetime(dateStr);
			goodsReview.setMemberSeqNo(user.getNo());

			if (goodsReview.getBuyGoodsSeqNo() != null) {
				BuyGoods buyGoods = buyGoodsRepository.findBySeqNo(goodsReview.getBuyGoodsSeqNo());
				buyGoods.setIsReviewExist(true);
				buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
				if(buyGoods.getReviewPoint() != null && buyGoods.getReviewPoint() > 0){
					kr.co.pplus.store.type.model.Page page = new kr.co.pplus.store.type.model.Page();
					page.setNo(buyGoods.getPageSeqNo());
					page = pageService.getPage(page);

					BolHistory history = new BolHistory();
					history.setAmount(buyGoods.getReviewPoint().floatValue());
					history.setUser(user);
					history.setSubject("리뷰작성 적립");
					history.setPrimaryType("increase");
					history.setSecondaryType("rewardReview");
					history.setTargetType("member");
					history.setTarget(user);
					history.setProperties(new HashMap<String, Object>());
					history.getProperties().put("지급처", page.getName());
					history.getProperties().put("적립유형", "리뷰작성");

					cashBolService.increaseBol(user, history);
				}
			}else if(goodsReview.getEventSeqNo() != null && goodsReview.getEventWinSeqNo() != null){
				eventService.updateGiftStatus(goodsReview.getEventSeqNo(), goodsReview.getEventWinSeqNo(), GiftStatus.REVIEW_WRITE.getStatus());
				EventWin eventWin = eventService.getEventWinBySeqNo(null, goodsReview.getEventSeqNo(), goodsReview.getEventWinSeqNo());
				if(eventWin.getGift() != null && eventWin.getGift().getReviewPoint() != null && eventWin.getGift().getReviewPoint() > 0){

					BolHistory history = new BolHistory();
					history.setAmount(eventWin.getGift().getReviewPoint().floatValue());
					history.setUser(user);
					history.setSubject("리뷰작성 적립");
					history.setPrimaryType("increase");
					history.setSecondaryType("rewardReview");
					history.setTargetType("member");
					history.setTarget(user);
					history.setProperties(new HashMap<String, Object>());

					kr.co.pplus.store.type.model.Page page = new kr.co.pplus.store.type.model.Page();
					page.setNo(goodsReview.getPageSeqNo());
					page = pageService.getPage(page);
					history.getProperties().put("지급처", page.getName());

					history.getProperties().put("적립유형", "리뷰작성");
					history.setUser(user);
					history.setPrimaryType("increase");
					cashBolService.increaseBol(user, history);
				}
			}
			goodsReview = goodsReviewRepository.saveAndFlush(goodsReview);

			return goodsReview;

		} catch (Exception e) {
			logger.error(AppUtil.excetionToString(e));
			throw new InvalidGoodsReviewException("goodsReview data", e);
		}

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public GoodsReview updateGoodsReview(User user, GoodsReview goodsReview) throws ResultCodeException {
		try {
			String dateStr = AppUtil.localDatetimeNowString();
			goodsReview.setRegDatetime(null);
			goodsReview.setModDatetime(dateStr);
			goodsReview.setMemberSeqNo(user.getNo());
			goodsReview = goodsReviewRepository.saveAndFlush(goodsReview);
			return goodsReview;
		} catch (Exception e) {
			logger.error(AppUtil.excetionToString(e));
			throw new InvalidGoodsReviewException("goodsReview data", e);
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public GoodsReview updateGoodsReviewReply(User user, GoodsReview goodsReview) throws ResultCodeException {
		kr.co.pplus.store.type.model.Page page = pageService.getPageByUser(user);

		if(!page.getNo().equals(goodsReview.getPageSeqNo())){
			throw new InvalidGoodsReviewException("goodsReview not page owner");
		}

		String dateStr = AppUtil.localDatetimeNowString();
		goodsReview.setRegDatetime(null);
		goodsReview.setModDatetime(dateStr);
		if(StringUtils.isNotEmpty(goodsReview.getReviewReply())){
			goodsReview.setReviewReplyDate(dateStr);
		}else{
			goodsReview.setReviewReplyDate(null);
		}
		goodsReview = goodsReviewRepository.saveAndFlush(goodsReview);
		return goodsReview;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void deleteGoodsReview(User user, Long seqNo) throws ResultCodeException {
		GoodsReview goodsReview = new GoodsReview();
		goodsReview.setSeqNo(seqNo);
		goodsReviewRepository.delete(goodsReview);
	}


	public GoodsReview selectGoodsReviewBySeqNo(Long seqNo){
		return goodsReviewRepository.findBySeqNo(seqNo);
	}

	public Page<GoodsReview> selectGoodsReview(Pageable pageable, Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo){
		Page<GoodsReview> page = null;
		if (pageSeqNo != null) {
			page = goodsReviewRepository.findAllByPageSeqNo(pageSeqNo, pageable);
		} else if (memberSeqNo != null) {
			if (goodsSeqNo != null) {

				page = goodsReviewRepository.findAllByMemberSeqNoAndGoodsSeqNo(memberSeqNo, goodsSeqNo, pageable);
			} else {
				page = goodsReviewRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
			}
		} else if (goodsSeqNo != null) {
			page = goodsReviewRepository.findAllByGoodsSeqNo(goodsSeqNo, pageable);
		} else {
			page = goodsReviewRepository.findAll(pageable);
		}
		return page;

	}

	public GoodsReviewDetail selectGoodsReviewDetailBySeqNo(Long seqNo){
		return goodsReviewDetailRepository.findBySeqNo(seqNo);
	}

	public Page<GoodsReviewDetail> selectGoodsReviewDetail(Pageable pageable, Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Long goodsPriceSeqNo){
		Page<GoodsReviewDetail> page = null;
		if (pageSeqNo != null) {
			page = goodsReviewDetailRepository.findAllByPageSeqNo(pageSeqNo, pageable);
		} else if (memberSeqNo != null) {
			if (goodsSeqNo != null) {

				page = goodsReviewDetailRepository.findAllByMemberSeqNoAndGoodsSeqNo(memberSeqNo, goodsSeqNo, pageable);
			} else {
				page = goodsReviewDetailRepository.findAllByMemberSeqNo(memberSeqNo, pageable);
			}
		} else if (goodsSeqNo != null) {
			page = goodsReviewDetailRepository.findAllByGoodsSeqNo(goodsSeqNo, pageable);
		} else if (goodsPriceSeqNo != null) {
			page = goodsReviewDetailRepository.findAllByGoodsPriceSeqNo(goodsPriceSeqNo, pageable);
		} else {
			page = goodsReviewDetailRepository.findAll(pageable);
		}
		return page;

	}

	public Integer countAllByMemberSeqNo(User user){
		return goodsReviewRepository.countAllByMemberSeqNo(user.getNo());
	}

	public Integer countGoodsReview(Long memberSeqNo, Long pageSeqNo, Long goodsSeqNo, Long goodsPriceSeqNo){
		Integer count = null;
		if (pageSeqNo != null) {
			count = goodsReviewRepository.countAllByPageSeqNo(pageSeqNo);
		} else if (memberSeqNo != null) {
			if (goodsSeqNo != null) {

				count = goodsReviewRepository.countAllByMemberSeqNoAndGoodsSeqNo(memberSeqNo, goodsSeqNo);
			} else {
				count = goodsReviewRepository.countAllByMemberSeqNo(memberSeqNo);
			}
		} else if (goodsSeqNo != null) {
			count = goodsReviewRepository.countAllByGoodsSeqNo(goodsSeqNo);
		} else if (goodsPriceSeqNo != null) {
			count = goodsReviewRepository.countAllByGoodsPriceSeqNo(goodsPriceSeqNo);
		} else {
			count = goodsReviewRepository.countAllBy();
		}
		return count;

	}
}
