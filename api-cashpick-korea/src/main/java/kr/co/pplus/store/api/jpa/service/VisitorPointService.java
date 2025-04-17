package kr.co.pplus.store.api.jpa.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.MsgRepository;
import kr.co.pplus.store.api.jpa.repository.PageRepository;
import kr.co.pplus.store.api.jpa.repository.PushTargetRepository;
import kr.co.pplus.store.api.jpa.repository.VisitorPointGiveHistoryRepository;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class VisitorPointService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(VisitorPointService.class);

	@Autowired
	VisitorPointGiveHistoryRepository visitorPointGiveHistoryRepository;


	@Autowired
	BolService bolService;

	@Autowired
	PageRepository pageRepository;

	@Autowired
	PlusJpaService plusJpaService;

	@Autowired
	MsgRepository msgRepository;

	@Autowired
	PushTargetRepository pushTargetRepository;

	@Autowired
	QueueService queueService;

	@Autowired
	PageAdvertiseHistoryService pageAdvertiseHistoryService;

	@Autowired
	EchossService echossService;

	@Value("${STORE.TYPE}")
	private String STORE_TYPE;

	public Page<VisitorPointGiveHistory> getVisitorPointGiveHistoryListByPageSeqNo(Long pageSeqNo, String startDatetime, String endDatetime, Pageable pageable) throws ResultCodeException {

		List<String> types = new ArrayList<>();
		types.add("benefit");

		return visitorPointGiveHistoryRepository.findAllByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqualAndTypeNotIn(pageSeqNo, startDatetime, endDatetime, types, pageable);
	}

	public Integer getCountVisitorPointGiveHistoryByPageSeqNo(Long pageSeqNo, String startDatetime, String endDatetime){
		return visitorPointGiveHistoryRepository.countByPageSeqNoAndRegDatetimeGreaterThanEqualAndRegDatetimeLessThanEqual(pageSeqNo, startDatetime, endDatetime);
	}

	public Integer getPriceVisitorPointGiveHistoryByPageSeqNo(Long pageSeqNo, String startDatetime, String endDatetime){
		return visitorPointGiveHistoryRepository.sumPrice(pageSeqNo, startDatetime, endDatetime);
	}

	public VisitorPointGiveHistory getFirstBenefit(Long memberSeqNo, Long pageSeqNo){
		return visitorPointGiveHistoryRepository.findByPageSeqNoAndReceiverSeqNoAndType(pageSeqNo, memberSeqNo, "benefit");
	}

	public VisitorPointGiveHistory getBySeqNo(Long seqNo){
		return  visitorPointGiveHistoryRepository.findBySeqNo(seqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int givePointWithStamp(User user, VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {
		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(visitorPointGiveHistory.getPageSeqNo());
		if(!AppUtil.isEmpty(visitorPointGiveHistory.getToken())){
			try {

				//{"result":{"merchant":"V00A114B008M00001","merchantName":"청풍칼국수","unique":"1002310","stamp":"B1725921"},"resCd":"0000","resMsg":"Okay"}
				String result = echossService.verificationToken(STORE_TYPE, visitorPointGiveHistory.getToken());
				JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();

				String resCd = jsonObject.get("resCd").getAsString();
				if(!resCd.equals("0000")){
					throw new NotMatchedValueException();
				}else{
					String merchant = jsonObject.get("result").getAsJsonObject().get("merchant").getAsString();
					if(!merchant.equals(page.getEchossId())){
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

		if (!user.getNo().equals(visitorPointGiveHistory.getReceiverSeqNo())){
			throw new NotPermissionException("user", "not matched user");
		}

		visitorPointGiveHistory.setSenderSeqNo(page.getMemberSeqNo());


		if(visitorPointGiveHistory.getType().equals("sns") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "sns") > 0){
			throw new AlreadyExistsException();
		}

		if(visitorPointGiveHistory.getType().equals("benefit") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "benefit") > 0){
			throw new AlreadyExistsException();
		}

		try{

			String dateStr = AppUtil.localDatetimeNowString();

			visitorPointGiveHistory.setIsPayment(false);
			visitorPointGiveHistory.setRegDatetime(dateStr);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				BolHistory bolHistory = new BolHistory();
				bolHistory.setAmount(visitorPointGiveHistory.getPrice().floatValue());
				bolHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				bolHistory.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());

				bolHistory.setPrimaryType("increase");

				bolHistory.setTargetType("member");
				bolHistory.setTargetSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
				bolHistory.setHistoryProp(new HashMap<String, Object>());
				bolHistory.getHistoryProp().put("지급처", page.getName());
				if(visitorPointGiveHistory.getType().equals("visit")){
					bolHistory.setSecondaryType("visit");
					bolHistory.setSubject("방문 적립");
					bolHistory.getHistoryProp().put("적립유형", "방문 적립");
				}else{
					bolHistory.setSecondaryType("sns");
					bolHistory.setSubject("SNS홍보 적립");
					bolHistory.getHistoryProp().put("적립유형", "SNS홍보 적립");
				}

				bolService.increaseBol(visitorPointGiveHistory.getReceiverSeqNo(), bolHistory);
			}

			visitorPointGiveHistory = visitorPointGiveHistoryRepository.saveAndFlush(visitorPointGiveHistory);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
				pageAdvertiseHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				pageAdvertiseHistory.setType(visitorPointGiveHistory.getType());
				pageAdvertiseHistory.setRegDatetime(dateStr);

				if(page.getVisitPointFee() != null){
					pageAdvertiseHistory.setPrice(page.getVisitPointFee());
				}else{
					pageAdvertiseHistory.setPrice(Const.ADS_COST);
				}
				pageAdvertiseHistory.setVisitorPointGiveHistorySeqNo(visitorPointGiveHistory.getSeqNo());
				pageAdvertiseHistoryService.save(pageAdvertiseHistory, visitorPointGiveHistory.getReceiverSeqNo());
			}



			Plus plus = new Plus();
			plus.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
			plus.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
			plus.setAgreement(false);
			plus.setBuyCount(0);
			plus.setBlock(false);
			plus.setPlusGiftReceived(false);
			plus.setPushActivate(true);
			plus.setRegDatetime(AppUtil.localDatetimeNowString());

			plusJpaService.insertPlus(plus);


			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int givePointFromUser(User user, VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {

		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(visitorPointGiveHistory.getPageSeqNo());


		if(AppUtil.isEmpty(visitorPointGiveHistory.getEchossId())){

			if(!page.getAuthCode().equals(visitorPointGiveHistory.getAuthCode())){
				throw new NotMatchedValueException();
			}

		}else{

			if(!page.getEchossId().equals(visitorPointGiveHistory.getEchossId())){
				throw new NotMatchedValueException();
			}
		}


		if (!user.getNo().equals(visitorPointGiveHistory.getReceiverSeqNo())){
			throw new NotPermissionException("user", "not matched user");
		}

		visitorPointGiveHistory.setSenderSeqNo(page.getMemberSeqNo());


		if(visitorPointGiveHistory.getType().equals("sns") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "sns") > 0){
			throw new AlreadyExistsException();
		}

		if(visitorPointGiveHistory.getType().equals("benefit") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "benefit") > 0){
			throw new AlreadyExistsException();
		}

		try{

			String dateStr = AppUtil.localDatetimeNowString();

			visitorPointGiveHistory.setIsPayment(false);
			visitorPointGiveHistory.setRegDatetime(dateStr);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				BolHistory bolHistory = new BolHistory();
				bolHistory.setAmount(visitorPointGiveHistory.getPrice().floatValue());
				bolHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				bolHistory.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());

				bolHistory.setPrimaryType("increase");

				bolHistory.setTargetType("member");
				bolHistory.setTargetSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
				bolHistory.setHistoryProp(new HashMap<String, Object>());
				bolHistory.getHistoryProp().put("지급처", page.getName());
				if(visitorPointGiveHistory.getType().equals("visit")){
					bolHistory.setSecondaryType("visit");
					bolHistory.setSubject("방문 적립");
					bolHistory.getHistoryProp().put("적립유형", "방문 적립");
				}else{
					bolHistory.setSecondaryType("sns");
					bolHistory.setSubject("SNS홍보 적립");
					bolHistory.getHistoryProp().put("적립유형", "SNS홍보 적립");
				}

				bolService.increaseBol(visitorPointGiveHistory.getReceiverSeqNo(), bolHistory);
			}

			visitorPointGiveHistory = visitorPointGiveHistoryRepository.saveAndFlush(visitorPointGiveHistory);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
				pageAdvertiseHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				pageAdvertiseHistory.setType(visitorPointGiveHistory.getType());
				pageAdvertiseHistory.setRegDatetime(dateStr);

				if(page.getVisitPointFee() != null){
					pageAdvertiseHistory.setPrice(page.getVisitPointFee());
				}else{
					pageAdvertiseHistory.setPrice(Const.ADS_COST);
				}
				pageAdvertiseHistory.setVisitorPointGiveHistorySeqNo(visitorPointGiveHistory.getSeqNo());
				pageAdvertiseHistoryService.save(pageAdvertiseHistory, visitorPointGiveHistory.getReceiverSeqNo());
			}



			Plus plus = new Plus();
			plus.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
			plus.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
			plus.setAgreement(false);
			plus.setBuyCount(0);
			plus.setBlock(false);
			plus.setPlusGiftReceived(false);
			plus.setPushActivate(true);
			plus.setRegDatetime(AppUtil.localDatetimeNowString());

			plusJpaService.insertPlus(plus);


			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public int givePoint(User user, VisitorPointGiveHistory visitorPointGiveHistory) throws ResultCodeException {

		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(visitorPointGiveHistory.getPageSeqNo());

		if (!user.getNo().equals(page.getMemberSeqNo())){
			throw new NotPermissionException("user", "not page owner");
		}

		if(visitorPointGiveHistory.getType().equals("sns") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "sns") > 0){
			throw new AlreadyExistsException();
		}

		if(visitorPointGiveHistory.getType().equals("benefit") && visitorPointGiveHistoryRepository.countByPageSeqNoAndReceiverSeqNoAndType(page.getSeqNo(), visitorPointGiveHistory.getReceiverSeqNo(), "benefit") > 0){
			throw new AlreadyExistsException();
		}

		try{

			String dateStr = AppUtil.localDatetimeNowString();

			visitorPointGiveHistory.setIsPayment(false);
			visitorPointGiveHistory.setRegDatetime(dateStr);
			if(!visitorPointGiveHistory.getType().equals("benefit")){
				BolHistory bolHistory = new BolHistory();
				bolHistory.setAmount(visitorPointGiveHistory.getPrice().floatValue());
				bolHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				bolHistory.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
				bolHistory.setPrimaryType("increase");

				bolHistory.setTargetType("member");
				bolHistory.setTargetSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
				bolHistory.setHistoryProp(new HashMap<String, Object>());
				bolHistory.getHistoryProp().put("지급처", page.getName());
				if(visitorPointGiveHistory.getType().equals("visit")){
					bolHistory.setSecondaryType("visit");
					bolHistory.setSubject("방문 적립");
					bolHistory.getHistoryProp().put("적립유형", "방문 적립");
				}else{
					bolHistory.setSecondaryType("sns");
					bolHistory.setSubject("SNS홍보 적립");
					bolHistory.getHistoryProp().put("적립유형", "SNS홍보 적립");
				}

				bolService.increaseBol(visitorPointGiveHistory.getReceiverSeqNo(), bolHistory);
			}


			visitorPointGiveHistory = visitorPointGiveHistoryRepository.saveAndFlush(visitorPointGiveHistory);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
				pageAdvertiseHistory.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
				pageAdvertiseHistory.setType(visitorPointGiveHistory.getType());
				pageAdvertiseHistory.setRegDatetime(dateStr);
				if(page.getVisitPointFee() != null){
					pageAdvertiseHistory.setPrice(page.getVisitPointFee());
				}else{
					pageAdvertiseHistory.setPrice(Const.ADS_COST);
				}
				pageAdvertiseHistory.setVisitorPointGiveHistorySeqNo(visitorPointGiveHistory.getSeqNo());
				pageAdvertiseHistoryService.save(pageAdvertiseHistory, visitorPointGiveHistory.getReceiverSeqNo());
			}



			Plus plus = new Plus();
			plus.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
			plus.setPageSeqNo(visitorPointGiveHistory.getPageSeqNo());
			plus.setAgreement(false);
			plus.setBuyCount(0);
			plus.setBlock(false);
			plus.setPlusGiftReceived(false);
			plus.setPushActivate(true);
			plus.setRegDatetime(AppUtil.localDatetimeNowString());

			plusJpaService.insertPlus(plus);

			if(!visitorPointGiveHistory.getType().equals("benefit")){
				MsgJpa msgJpa = new MsgJpa();
				msgJpa.setSeqNo(null);
				msgJpa.setIncludeMe(false);
				msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
				msgJpa.setStatus(Const.MSG_STATUS_READY);
				msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
				msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
				msgJpa.setMoveType2(Const.MOVE_TYPE_BOLHISTORY);
				msgJpa.setSubject("캐시가 지급되었습니다.");
				if(visitorPointGiveHistory.getType().equals("visit")){
					msgJpa.setContents("방문 적립 : "+visitorPointGiveHistory.getPrice()+"원");
				}else{
					msgJpa.setContents("SNS홍보 적립 : "+visitorPointGiveHistory.getPrice()+"원");
				}

				msgJpa.setMoveSeqNo(visitorPointGiveHistory.getPageSeqNo());
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
				pushTargetJpa.setMemberSeqNo(visitorPointGiveHistory.getReceiverSeqNo());
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
				msg.setMoveType2(Const.MOVE_TYPE_BOLHISTORY);
				msg.setSubject("캐시가 지급되었습니다.");
				msg.setContents(visitorPointGiveHistory.getPrice()+"원");
				msg.setMoveTarget(new NoOnlyKey(visitorPointGiveHistory.getPageSeqNo()));
				queueService.sendOnlyPush(msg);
			}


			return Const.E_SUCCESS;
		}catch (Exception e){
			logger.error(e.toString());
			throw new InvalidCashException();
		}
	}
}
