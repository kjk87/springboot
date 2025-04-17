package kr.co.pplus.store.api.jpa.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.MsgRepository;
import kr.co.pplus.store.api.jpa.repository.PushTargetRepository;
import kr.co.pplus.store.api.jpa.repository.SubscriptionDownloadRepository;
import kr.co.pplus.store.api.jpa.repository.SubscriptionLogRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.MsgOnly;
import kr.co.pplus.store.type.model.NoOnlyKey;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class SubscriptionService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

	@Autowired
	SubscriptionDownloadRepository subscriptionDownloadRepository;

	@Autowired
	SubscriptionLogRepository subscriptionLogRepository;

	@Autowired
	ProductService productService;

	@Autowired
	PageAdvertiseHistoryService pageAdvertiseHistoryService;

	@Autowired
	PlusJpaService plusJpaService;

	@Autowired
	MsgRepository msgRepository;

	@Autowired
	PushTargetRepository pushTargetRepository;

	@Autowired
	QueueService queueService;

	@Autowired
	EchossService echossService;

	@Value("${STORE.TYPE}")
	private String STORE_TYPE;

	public Integer getSubscriptionDownloadCountByMemberSeqNoAndStatus(Long memberSeqNo){
		return subscriptionDownloadRepository.countByMemberSeqNoAndStatus(memberSeqNo, 1);
	}

	public Map<String, Object> getSubscriptionCountByProductPriceSeqNo(Long productPriceSeqNo) throws ResultCodeException {

		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("downCount", subscriptionDownloadRepository.countByProductPriceSeqNo(productPriceSeqNo));
		ret.put("useCount", subscriptionLogRepository.countByProductPriceSeqNo(productPriceSeqNo));

		return ret;

	}

	public Page<SubscriptionDownload> getSubscriptionDownloadListByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable){
		return subscriptionDownloadRepository.findAllByProductPriceSeqNo(productPriceSeqNo, pageable);
	}

	public Page<SubscriptionDownload> getSubscriptionDownloadListByMemberSeqNo(Long memberSeqNo, Pageable pageable){
		return subscriptionDownloadRepository.findAllByMemberSeqNoOrderByStatusAscSeqNoDesc(memberSeqNo, pageable);
	}

	public Page<SubscriptionLog> getSubscriptionLogListByProductPriceSeqNo(Long productPriceSeqNo, Pageable pageable){
		return subscriptionLogRepository.findAllByProductPriceSeqNo(productPriceSeqNo, pageable);
	}

	public List<SubscriptionLog> getSubscriptionLogListBySubscriptionDownloadSeqNo(Long subscriptionDownloadSeqNo, String sort){
		if(sort.equals("recent")){
			return subscriptionLogRepository.findAllBySubscriptionSeqNoOrderBySeqNoDesc(subscriptionDownloadSeqNo);
		}else{
			return subscriptionLogRepository.findAllBySubscriptionSeqNoOrderBySeqNoAsc(subscriptionDownloadSeqNo);
		}

	}

	public SubscriptionDownload getSubscriptionDownloadBySeqNo(Long seqNo){
		return subscriptionDownloadRepository.findBySeqNo(seqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public SubscriptionDownload download(User pageUser, Long memberSeqNo, Long productPriceSeqNo) throws ResultCodeException{

		String dateStr = AppUtil.localDatetimeNowString();

		ProductPrice productPrice = productService.getProductPriceBySeqNo(productPriceSeqNo);

		if(!pageUser.getNo().equals(productPrice.getPage().getMemberSeqNo())){
			throw new NotPermissionException("user", "not page owner");
		}


		if(productPrice.getIsSubscription() !=null && productPrice.getIsSubscription() && subscriptionDownloadRepository.countByMemberSeqNoAndProductPriceSeqNoAndStatus(memberSeqNo, productPriceSeqNo, 1) > 0){
			throw new AlreadyExistsException();
		}

		LocalDate nowDate = LocalDate.now();
		SubscriptionDownload subscriptionDownload = new SubscriptionDownload();
		subscriptionDownload.setExpireDate(nowDate.plusDays(productPrice.getRemainDays()));
		subscriptionDownload.setMemberSeqNo(memberSeqNo);
		subscriptionDownload.setProductPriceSeqNo(productPriceSeqNo);
		subscriptionDownload.setRegDatetime(dateStr);
		subscriptionDownload.setStatus(1);
		subscriptionDownload.setName(productPrice.getProduct().getName());
		subscriptionDownload.setUseCondition(productPrice.getProduct().getContents());

		if(productPrice.getIsSubscription()){
			subscriptionDownload.setType("subscription");
			subscriptionDownload.setHaveCount(productPrice.getTimes());
			subscriptionDownload.setUseCount(0);
		}else if(productPrice.getIsPrepayment()){
			subscriptionDownload.setType("prepayment");
			subscriptionDownload.setHavePrice(productPrice.getOriginPrice().intValue());
			subscriptionDownload.setUsePrice(0);
		}

		subscriptionDownload = subscriptionDownloadRepository.saveAndFlush(subscriptionDownload);



		Plus plus = new Plus();
		plus.setMemberSeqNo(memberSeqNo);
		plus.setPageSeqNo(productPrice.getPageSeqNo());
		plus.setAgreement(false);
		plus.setBuyCount(0);
		plus.setBlock(false);
		plus.setPlusGiftReceived(false);
		plus.setPushActivate(true);
		plus.setRegDatetime(dateStr);

		plusJpaService.insertPlus(plus);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_USE);
				msgJpa.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_USE);
				msgJpa.setSubject("선불 이용권 발급되었습니다.");
			}

			msgJpa.setContents(subscriptionDownload.getName());

			msgJpa.setMoveSeqNo(subscriptionDownload.getSeqNo());
			msgJpa.setMemberSeqNo(productPrice.getPage().getMemberSeqNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(subscriptionDownload.getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_USER);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_USE);
				msg.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_USE);
				msg.setSubject("선불 이용권 발급되었습니다.");
			}
			msg.setContents(subscriptionDownload.getName());
			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error("error : "+e.toString());
		}

		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
		pageAdvertiseHistory.setPageSeqNo(productPrice.getPageSeqNo());
		pageAdvertiseHistory.setType(subscriptionDownload.getType());
		pageAdvertiseHistory.setRegDatetime(dateStr);

		if(productPrice.getPage().getSubscribeFee() != null){
			pageAdvertiseHistory.setPrice(productPrice.getPage().getSubscribeFee());
		}else{
			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_PUBLISH_ADS_COST);
		}

		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
		pageAdvertiseHistory = pageAdvertiseHistoryService.save(pageAdvertiseHistory, subscriptionDownload.getMemberSeqNo());
		logger.error("pageAdvertiseHistory : "+pageAdvertiseHistory.getSeqNo());

		return subscriptionDownload;

	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public SubscriptionDownload downloadWithStamp(Long memberSeqNo, Long productPriceSeqNo, String token) throws ResultCodeException{

		ProductPrice productPrice = productService.getProductPriceBySeqNo(productPriceSeqNo);

		if(!AppUtil.isEmpty(token)){
			try {

				//{"result":{"merchant":"V00A114B008M00001","merchantName":"청풍칼국수","unique":"1002310","stamp":"B1725921"},"resCd":"0000","resMsg":"Okay"}
				String result = echossService.verificationToken(STORE_TYPE, token);
				JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

				String resCd = jsonObject.get("resCd").getAsString();
				if(!resCd.equals("0000")){
					throw new NotMatchedValueException();
				}else{
					String merchant = jsonObject.get("result").getAsJsonObject().get("merchant").getAsString();
					if(!merchant.equals(productPrice.getPage().getEchossId())){
						throw new NotMatchedValueException();
					}
				}

			}catch (Exception e){
				logger.error(e.toString());
				throw new NotMatchedValueException();
			}
		}else {
			throw new NotMatchedValueException();
		}

		String dateStr = AppUtil.localDatetimeNowString();

		if(productPrice.getIsSubscription() !=null && productPrice.getIsSubscription() && subscriptionDownloadRepository.countByMemberSeqNoAndProductPriceSeqNoAndStatus(memberSeqNo, productPriceSeqNo, 1) > 0){
			throw new AlreadyExistsException();
		}

		LocalDate nowDate = LocalDate.now();
		SubscriptionDownload subscriptionDownload = new SubscriptionDownload();
		subscriptionDownload.setExpireDate(nowDate.plusDays(productPrice.getRemainDays()));
		subscriptionDownload.setMemberSeqNo(memberSeqNo);
		subscriptionDownload.setProductPriceSeqNo(productPriceSeqNo);
		subscriptionDownload.setRegDatetime(dateStr);
		subscriptionDownload.setStatus(1);
		subscriptionDownload.setName(productPrice.getProduct().getName());
		subscriptionDownload.setUseCondition(productPrice.getProduct().getContents());


		if(productPrice.getIsSubscription()){
			subscriptionDownload.setType("subscription");
			subscriptionDownload.setHaveCount(productPrice.getTimes());
			subscriptionDownload.setUseCount(0);
		}else if(productPrice.getIsPrepayment()){
			subscriptionDownload.setType("prepayment");
			subscriptionDownload.setHavePrice(productPrice.getOriginPrice().intValue());
			subscriptionDownload.setUsePrice(0);
		}

		subscriptionDownload = subscriptionDownloadRepository.saveAndFlush(subscriptionDownload);

		Plus plus = new Plus();
		plus.setMemberSeqNo(memberSeqNo);
		plus.setPageSeqNo(productPrice.getPageSeqNo());
		plus.setAgreement(false);
		plus.setBuyCount(0);
		plus.setBlock(false);
		plus.setPlusGiftReceived(false);
		plus.setPushActivate(true);
		plus.setRegDatetime(dateStr);

		plusJpaService.insertPlus(plus);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msgJpa.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msgJpa.setSubject("선불 이용권 발급되었습니다.");
			}

			msgJpa.setContents(subscriptionDownload.getName());

			msgJpa.setMoveSeqNo(subscriptionDownload.getProductPriceSeqNo());
			msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(productPrice.getPage().getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_BIZ);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msg.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msg.setSubject("선불 이용권 발급되었습니다.");
			}
			msg.setContents(subscriptionDownload.getName());
			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getProductPriceSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error(e.toString());
		}

		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
		pageAdvertiseHistory.setPageSeqNo(productPrice.getPageSeqNo());
		pageAdvertiseHistory.setType(subscriptionDownload.getType());
		pageAdvertiseHistory.setRegDatetime(dateStr);

		if(productPrice.getPage().getSubscribeFee() != null){
			pageAdvertiseHistory.setPrice(productPrice.getPage().getSubscribeFee());
		}else{
			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_PUBLISH_ADS_COST);
		}

		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
		pageAdvertiseHistory = pageAdvertiseHistoryService.save(pageAdvertiseHistory, subscriptionDownload.getMemberSeqNo());


		return subscriptionDownload;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public SubscriptionDownload downloadFromUser(Long memberSeqNo, Long productPriceSeqNo, String authCode, String echossId) throws ResultCodeException{
		String dateStr = AppUtil.localDatetimeNowString();

		ProductPrice productPrice = productService.getProductPriceBySeqNo(productPriceSeqNo);


		if(AppUtil.isEmpty(echossId)){

			if(!productPrice.getPage().getAuthCode().equals(authCode)){
				throw new NotMatchedValueException();
			}
		}else{

			if(!productPrice.getPage().getEchossId().equals(echossId)){
				throw new NotMatchedValueException();
			}
		}

		if(productPrice.getIsSubscription() !=null && productPrice.getIsSubscription() && subscriptionDownloadRepository.countByMemberSeqNoAndProductPriceSeqNoAndStatus(memberSeqNo, productPriceSeqNo, 1) > 0){
			throw new AlreadyExistsException();
		}

		LocalDate nowDate = LocalDate.now();
		SubscriptionDownload subscriptionDownload = new SubscriptionDownload();
		subscriptionDownload.setExpireDate(nowDate.plusDays(productPrice.getRemainDays()));
		subscriptionDownload.setMemberSeqNo(memberSeqNo);
		subscriptionDownload.setProductPriceSeqNo(productPriceSeqNo);
		subscriptionDownload.setRegDatetime(dateStr);
		subscriptionDownload.setStatus(1);
		subscriptionDownload.setName(productPrice.getProduct().getName());
		subscriptionDownload.setUseCondition(productPrice.getProduct().getContents());


		if(productPrice.getIsSubscription()){
			subscriptionDownload.setType("subscription");
			subscriptionDownload.setHaveCount(productPrice.getTimes());
			subscriptionDownload.setUseCount(0);
		}else if(productPrice.getIsPrepayment()){
			subscriptionDownload.setType("prepayment");
			subscriptionDownload.setHavePrice(productPrice.getOriginPrice().intValue());
			subscriptionDownload.setUsePrice(0);
		}

		subscriptionDownload = subscriptionDownloadRepository.saveAndFlush(subscriptionDownload);

		Plus plus = new Plus();
		plus.setMemberSeqNo(memberSeqNo);
		plus.setPageSeqNo(productPrice.getPageSeqNo());
		plus.setAgreement(false);
		plus.setBuyCount(0);
		plus.setBlock(false);
		plus.setPlusGiftReceived(false);
		plus.setPushActivate(true);
		plus.setRegDatetime(dateStr);

		plusJpaService.insertPlus(plus);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msgJpa.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msgJpa.setSubject("선불 이용권 발급되었습니다.");
			}

			msgJpa.setContents(subscriptionDownload.getName());

			msgJpa.setMoveSeqNo(subscriptionDownload.getProductPriceSeqNo());
			msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(productPrice.getPage().getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_BIZ);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msg.setSubject("선불 금액권 발급되었습니다.");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msg.setSubject("선불 이용권 발급되었습니다.");
			}
			msg.setContents(subscriptionDownload.getName());
			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getProductPriceSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error(e.toString());
		}

		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
		pageAdvertiseHistory.setPageSeqNo(productPrice.getPageSeqNo());
		pageAdvertiseHistory.setType(subscriptionDownload.getType());
		pageAdvertiseHistory.setRegDatetime(dateStr);

		if(productPrice.getPage().getSubscribeFee() != null){
			pageAdvertiseHistory.setPrice(productPrice.getPage().getSubscribeFee());
		}else{
			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_PUBLISH_ADS_COST);
		}

		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
		pageAdvertiseHistory = pageAdvertiseHistoryService.save(pageAdvertiseHistory, subscriptionDownload.getMemberSeqNo());
		logger.error("pageAdvertiseHistory : "+pageAdvertiseHistory.getSeqNo());


		return subscriptionDownload;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int use(User pageUser, Long memberSeqNo, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice) throws ResultCodeException {

		SubscriptionDownload subscriptionDownload = getSubscriptionDownloadBySeqNo(subscriptionDownloadSeqNo);

		if(!pageUser.getNo().equals(subscriptionDownload.getProductPrice().getPage().getMemberSeqNo())){
			throw new NotPermissionException("user", "not page owner");
		}

		if(subscriptionDownload.getStatus() != 1){
			throw new ExpiredException();
		}


		String dateStr = AppUtil.localDatetimeNowString();

		if(subscriptionDownload.getType() == null || subscriptionDownload.getType().equals("subscription")){
			subscriptionDownload.setType("subscription");
			if(subscriptionDownload.getUseCount() + useCount > subscriptionDownload.getHaveCount()){
				throw new AlreadyLimitException();
			}

			for(int i = 0; i < useCount; i++){
				SubscriptionLog subscriptionLog = new SubscriptionLog();
				subscriptionLog.setMemberSeqNo(memberSeqNo);
				subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
				subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
				subscriptionLog.setRegDatetime(dateStr);
				subscriptionLogRepository.save(subscriptionLog);
			}

			subscriptionDownload.setUseCount(subscriptionDownload.getUseCount()+useCount);
			if(subscriptionDownload.getUseCount() >= subscriptionDownload.getHaveCount()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}

		}else if(subscriptionDownload.getType().equals("prepayment")){
			if(subscriptionDownload.getUsePrice() + usePrice > subscriptionDownload.getHavePrice()){
				throw new AlreadyLimitException();
			}

			SubscriptionLog subscriptionLog = new SubscriptionLog();
			subscriptionLog.setMemberSeqNo(memberSeqNo);
			subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
			subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
			subscriptionLog.setRegDatetime(dateStr);
			subscriptionLog.setUsePrice(usePrice);
			subscriptionLogRepository.save(subscriptionLog);

			subscriptionDownload.setUsePrice(subscriptionDownload.getUsePrice()+usePrice);
			if(subscriptionDownload.getUsePrice() >= subscriptionDownload.getHavePrice()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}
		}

		subscriptionDownload = subscriptionDownloadRepository.saveAndFlush(subscriptionDownload);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_USE);
				msgJpa.setSubject("선불 금액권을 사용하였습니다.");
				msgJpa.setContents(usePrice+"원 사용");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_USE);
				msgJpa.setSubject("선불 이용권을 사용하였습니다.");
				msgJpa.setContents(subscriptionDownload.getName());
			}

			msgJpa.setMoveSeqNo(subscriptionDownload.getSeqNo());
			msgJpa.setMemberSeqNo(pageUser.getNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(subscriptionDownload.getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_USER);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_USE);
				msg.setSubject("선불 금액권을 사용하였습니다.");
				msg.setContents(usePrice+"원 사용");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_USE);
				msg.setSubject("선불 이용권을 사용하였습니다.");
				msg.setContents(subscriptionDownload.getName());
			}

			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error(e.toString());
		}

//		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
//		pageAdvertiseHistory.setPageSeqNo(subscriptionDownload.getProductPrice().getPageSeqNo());
//		pageAdvertiseHistory.setType(subscriptionDownload.getType());
//		pageAdvertiseHistory.setRegDatetime(dateStr);
//
//		if(subscriptionDownload.getProductPrice().getPage().getSubscribeFee() != null){
//			pageAdvertiseHistory.setPrice(subscriptionDownload.getProductPrice().getPage().getSubscribeFee());
//		}else{
//			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_ADS_COST);
//		}
//
//		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
//		pageAdvertiseHistoryService.save(pageAdvertiseHistory);

		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int use(Long memberSeqNo, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice, String authCode, String echossId) throws ResultCodeException {

		SubscriptionDownload subscriptionDownload = getSubscriptionDownloadBySeqNo(subscriptionDownloadSeqNo);
		if(AppUtil.isEmpty(echossId)){

			if(!subscriptionDownload.getProductPrice().getPage().getAuthCode().equals(authCode)){
				throw new NotMatchedValueException();
			}
		}else{

			if(!subscriptionDownload.getProductPrice().getPage().getEchossId().equals(echossId)){
				throw new NotMatchedValueException();
			}
		}

		if(subscriptionDownload.getStatus() != 1){
			throw new ExpiredException();
		}

		String dateStr = AppUtil.localDatetimeNowString();

		if(subscriptionDownload.getType() == null || subscriptionDownload.getType().equals("subscription")){
			subscriptionDownload.setType("subscription");
			if(subscriptionDownload.getUseCount() + useCount > subscriptionDownload.getHaveCount()){
				throw new AlreadyLimitException();
			}

			for(int i = 0; i < useCount; i++){
				SubscriptionLog subscriptionLog = new SubscriptionLog();
				subscriptionLog.setMemberSeqNo(memberSeqNo);
				subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
				subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
				subscriptionLog.setRegDatetime(dateStr);
				subscriptionLogRepository.save(subscriptionLog);
			}

			subscriptionDownload.setUseCount(subscriptionDownload.getUseCount()+useCount);
			if(subscriptionDownload.getUseCount() >= subscriptionDownload.getHaveCount()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}

		}else if(subscriptionDownload.getType().equals("prepayment")){
			if(subscriptionDownload.getUsePrice() + usePrice > subscriptionDownload.getHavePrice()){
				throw new AlreadyLimitException();
			}

			SubscriptionLog subscriptionLog = new SubscriptionLog();
			subscriptionLog.setMemberSeqNo(memberSeqNo);
			subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
			subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
			subscriptionLog.setRegDatetime(dateStr);
			subscriptionLog.setUsePrice(usePrice);
			subscriptionLogRepository.save(subscriptionLog);

			subscriptionDownload.setUsePrice(subscriptionDownload.getUsePrice()+usePrice);
			if(subscriptionDownload.getUsePrice() >= subscriptionDownload.getHavePrice()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}
		}

		subscriptionDownloadRepository.save(subscriptionDownload);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msgJpa.setSubject("선불 금액권을 사용하였습니다.");
				msgJpa.setContents(usePrice+"원 사용");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msgJpa.setSubject("선불 이용권을 사용하였습니다.");
				msgJpa.setContents(subscriptionDownload.getName());
			}



			msgJpa.setMoveSeqNo(subscriptionDownload.getProductPriceSeqNo());
			msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(subscriptionDownload.getProductPrice().getPage().getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_BIZ);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msg.setSubject("선불 금액권을 사용하였습니다.");
				msg.setContents(usePrice+"원 사용");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msg.setSubject("선불 이용권을 사용하였습니다.");
				msg.setContents(subscriptionDownload.getName());
			}

			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getProductPriceSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error(e.toString());
		}

//		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
//		pageAdvertiseHistory.setPageSeqNo(subscriptionDownload.getProductPrice().getPageSeqNo());
//		pageAdvertiseHistory.setType(subscriptionDownload.getType());
//		pageAdvertiseHistory.setRegDatetime(dateStr);
//
//		if(subscriptionDownload.getProductPrice().getPage().getSubscribeFee() != null){
//			pageAdvertiseHistory.setPrice(subscriptionDownload.getProductPrice().getPage().getSubscribeFee());
//		}else{
//			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_ADS_COST);
//		}
//
//		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
//		pageAdvertiseHistoryService.save(pageAdvertiseHistory);

		return Const.E_SUCCESS;
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int useWithStamp(Long memberSeqNo, Long subscriptionDownloadSeqNo, Integer useCount, Integer usePrice, String token) throws ResultCodeException {

		SubscriptionDownload subscriptionDownload = getSubscriptionDownloadBySeqNo(subscriptionDownloadSeqNo);

		if(!AppUtil.isEmpty(token)){
			try {

				//{"result":{"merchant":"V00A114B008M00001","merchantName":"청풍칼국수","unique":"1002310","stamp":"B1725921"},"resCd":"0000","resMsg":"Okay"}
				String result = echossService.verificationToken(STORE_TYPE, token);
				JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

				String resCd = jsonObject.get("resCd").getAsString();
				if(!resCd.equals("0000")){
					throw new NotMatchedValueException();
				}else{
					String merchant = jsonObject.get("result").getAsJsonObject().get("merchant").getAsString();
					if(!merchant.equals(subscriptionDownload.getProductPrice().getPage().getEchossId())){
						throw new NotMatchedValueException();
					}
				}

			}catch (Exception e){
				logger.error(e.toString());
				throw new NotMatchedValueException();
			}
		}else {
			throw new NotMatchedValueException();
		}

		if(subscriptionDownload.getStatus() != 1){
			throw new ExpiredException();
		}

		String dateStr = AppUtil.localDatetimeNowString();

		if(subscriptionDownload.getType() == null || subscriptionDownload.getType().equals("subscription")){
			subscriptionDownload.setType("subscription");
			if(subscriptionDownload.getUseCount() + useCount > subscriptionDownload.getHaveCount()){
				throw new AlreadyLimitException();
			}

			for(int i = 0; i < useCount; i++){
				SubscriptionLog subscriptionLog = new SubscriptionLog();
				subscriptionLog.setMemberSeqNo(memberSeqNo);
				subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
				subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
				subscriptionLog.setRegDatetime(dateStr);
				subscriptionLogRepository.save(subscriptionLog);
			}

			subscriptionDownload.setUseCount(subscriptionDownload.getUseCount()+useCount);
			if(subscriptionDownload.getUseCount() >= subscriptionDownload.getHaveCount()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}

		}else if(subscriptionDownload.getType().equals("prepayment")){
			if(subscriptionDownload.getUsePrice() + usePrice > subscriptionDownload.getHavePrice()){
				throw new AlreadyLimitException();
			}

			SubscriptionLog subscriptionLog = new SubscriptionLog();
			subscriptionLog.setMemberSeqNo(memberSeqNo);
			subscriptionLog.setProductPriceSeqNo(subscriptionDownload.getProductPriceSeqNo());
			subscriptionLog.setSubscriptionSeqNo(subscriptionDownload.getSeqNo());
			subscriptionLog.setRegDatetime(dateStr);
			subscriptionLog.setUsePrice(usePrice);
			subscriptionLogRepository.save(subscriptionLog);

			subscriptionDownload.setUsePrice(subscriptionDownload.getUsePrice()+usePrice);
			if(subscriptionDownload.getUsePrice() >= subscriptionDownload.getHavePrice()){
				subscriptionDownload.setCompleteDatetime(dateStr);
				subscriptionDownload.setStatus(2);
			}
		}

		subscriptionDownloadRepository.save(subscriptionDownload);

		try {
			MsgJpa msgJpa = new MsgJpa();
			msgJpa.setSeqNo(null);
			msgJpa.setIncludeMe(false);
			msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
			msgJpa.setStatus(Const.MSG_STATUS_READY);
			msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
			msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);


			if(subscriptionDownload.getType().equals("prepayment")){
				msgJpa.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msgJpa.setSubject("선불 금액권을 사용하였습니다.");
				msgJpa.setContents(usePrice+"원 사용");
			}else{
				msgJpa.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msgJpa.setSubject("선불 이용권을 사용하였습니다.");
				msgJpa.setContents(subscriptionDownload.getName());
			}



			msgJpa.setMoveSeqNo(subscriptionDownload.getProductPriceSeqNo());
			msgJpa.setMemberSeqNo(StoreUtil.getCommonAdmin().getNo());
			msgJpa.setReserved(false);
			msgJpa.setPayType(Const.MSG_PAY_TYPE_NONE);
			msgJpa.setStatus("ready");
			msgJpa.setTotalPrice(0L);
			msgJpa.setRefundPrice(0L);
			msgJpa.setTargetCount(1);
			msgJpa.setSuccCount(0);
			msgJpa.setFailCount(0);
			msgJpa.setReadCount(0);
			msgJpa.setRegDatetime(dateStr);
			msgJpa = msgRepository.saveAndFlush(msgJpa);

			PushTargetJpa pushTargetJpa = new PushTargetJpa();
			pushTargetJpa.setMsgSeqNo(msgJpa.getSeqNo());
			pushTargetJpa.setMemberSeqNo(subscriptionDownload.getProductPrice().getPage().getMemberSeqNo());
			pushTargetRepository.saveAndFlush(pushTargetJpa);

			MsgOnly msg = new MsgOnly();
			msg.setNo(msgJpa.getSeqNo());
			msg.setIncludeMe(false);
			msg.setInput(Const.MSG_INPUT_SYSTEM);
			msg.setStatus(Const.MSG_STATUS_READY);
			msg.setType(Const.MSG_TYPE_PUSH);
			msg.setMoveType1(Const.MOVE_TYPE_INNER);
			msg.setPushCase(Const.BIZ_PUSH_SENDPUSH);
			msg.setAppType(Const.APP_TYPE_BIZ);

			if(subscriptionDownload.getType().equals("prepayment")){
				msg.setMoveType2(Const.MOVE_TYPE_PREPAYMENT_DETAIL);
				msg.setSubject("선불 금액권을 사용하였습니다.");
				msg.setContents(usePrice+"원 사용");
			}else{
				msg.setMoveType2(Const.MOVE_TYPE_SUBSCRIPTION_DETAIL);
				msg.setSubject("선불 이용권을 사용하였습니다.");
				msg.setContents(subscriptionDownload.getName());
			}

			msg.setMoveTarget(new NoOnlyKey(subscriptionDownload.getProductPriceSeqNo()));
			queueService.sendOnlyPush(msg);
		}catch (Exception e){
			logger.error(e.toString());
		}

//		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
//		pageAdvertiseHistory.setPageSeqNo(subscriptionDownload.getProductPrice().getPageSeqNo());
//		pageAdvertiseHistory.setType(subscriptionDownload.getType());
//		pageAdvertiseHistory.setRegDatetime(dateStr);
//
//		if(subscriptionDownload.getProductPrice().getPage().getSubscribeFee() != null){
//			pageAdvertiseHistory.setPrice(subscriptionDownload.getProductPrice().getPage().getSubscribeFee());
//		}else{
//			pageAdvertiseHistory.setPrice(Const.SUBSCRIPTION_ADS_COST);
//		}
//
//		pageAdvertiseHistory.setSubscriptionDownloadSeqNo(subscriptionDownload.getSeqNo());
//		pageAdvertiseHistoryService.save(pageAdvertiseHistory);

		return Const.E_SUCCESS;
	}
}
