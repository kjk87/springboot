package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class MsgService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(MsgService.class);
	
//	@Autowired
//	MsgDao dao;
	
	@Autowired
	CommonService commonSvc;
	
	@Autowired
	CashBolService cashSvc;
	
	@Autowired
	UserService userSvc;

	@Autowired
	CustomerService customerService;
	
	@Autowired
    MsgProducer producer;

	@Value("${STORE.SKBB_DBAGENTID}")
	public String SKBB_DBAGENTID = "5567";
	
	@Value("${STORE.SMS_CERT_SENDER_NUM}")
	public String SMS_CERT_SENDER_NUM = "02-6315-1234";

	private int getSmsPrice(MsgOnly msg){
		int price = 0;
		boolean ad = false;
		if (msg.getProperties() != null && msg.getProperties().containsKey("advertise"))
			ad = (Boolean)msg.getProperties().get("advertise");

		if (msg.getType().equalsIgnoreCase("sms"))
			price = ad ? commonSvc.getAdvertiseSmsPrice(msg.getAuthor().getCountry()) : commonSvc.getSmsPrice(msg.getAuthor().getCountry());
		else if (msg.getType().equalsIgnoreCase("lms"))
			price = ad ? commonSvc.getAdvertiseLmsPrice(msg.getAuthor().getCountry()) : commonSvc.getLmsPrice(msg.getAuthor().getCountry());
		else if (msg.getType().equalsIgnoreCase("push"))
			price = commonSvc.getPushPrice(msg.getAuthor().getCountry());
		return price;
	}
	
	private long checkCash(MsgOnly msg, Integer price) throws ResultCodeException {
		long totalPrice = 0;
		totalPrice = msg.getTargetCount() * price;
		
		if (msg.getTotalPrice() != null && msg.getTotalPrice() != totalPrice) {
			msg.setTotalPrice(totalPrice);
			throw new NotMatchedCashException("totalPrice", totalPrice);
		}
		
		if (msg.getAuthor().getCash() == null || totalPrice > msg.getAuthor().getCash())
			throw new NotEnoughCashException();
		
		return totalPrice;
	}
	
	public void initMsgOnly(MsgOnly msg) {
		if (msg.getIncludeMe() == null)
			msg.setIncludeMe(false);
		
		if (msg.getReserved() == null)
			msg.setReserved(false);
		
		if (msg.getAppType() == null)
			msg.setAppType(Const.APP_TYPE_USER);

		if (msg.getStatus() == null)
			msg.setStatus(msg.getReserved() == true ? "reserved" : "ready");
		
		if (msg.getPushCase() == null)
			msg.setPushCase(Const.PUSH_FORCED);
		
		if (msg.getPayType() == null)
			msg.setPayType(Const.MSG_PAY_TYPE_NONE);
		
		if (msg.getTotalPrice() == null)
			msg.setTotalPrice(0L);
		
		if (msg.getInput() == null)
			msg.setInput("system");

		if(msg.getTargetAll() == null){
			msg.setTargetAll(false);
		}
		
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertSmsMsg(MsgOnly msg, List<SmsTarget> targetList) throws ResultCodeException {
		if (targetList == null || targetList.size() == 0)
			throw new NotFoundTargetException("targetList", "empty");
		
		initMsgOnly(msg);
	
		msg.setTargetCount(targetList.size());

		Integer price = getSmsPrice(msg);
		Long totalPrice = 0L;
		List<Customer> customerList = null;
		if(msg.getTargetAll()){
			customerList = customerService.getCustomerListAllByPageSeqNo(msg.getPage().getNo());
			totalPrice = (long)(customerList.size() * price);

			if (msg.getAuthor().getCash() == null || totalPrice > msg.getAuthor().getCash()){
				throw new NotEnoughCashException();
			}

		}else{
			totalPrice = checkCash(msg, price);
		}
		msg.setTotalPrice(totalPrice);
		if (totalPrice > 0) {
			msg.setPayType(Const.MSG_PAY_TYPE_PAY);
		} else {
			msg.setPayType(Const.MSG_PAY_TYPE_NONE);
		}

		int effected = sqlSession.insert("Msg.insertMsg", msg);
		if (effected > 0) {
			if(msg.getTargetAll()){
				ParamMap map = new ParamMap() ;
				map.put("msg", msg) ;
				map.put("price", price) ;
				map.put("list",customerList);
				sqlSession.insert("Msg.insertSmsTargetCustomerList", map) ;
			}else{
				for (SmsTarget target : targetList) {
					if (target.getStatus() == null)
						target.setStatus("ready");
					target.setSmsPrice(price);
					target.setMobile(StoreUtil.getValidatePhoneNumber(target.getMobile()));
					ParamMap map = new ParamMap() ;
					map.put("msg", msg) ;
					map.put("target", target) ;
					sqlSession.insert("Msg.insertSmsTarget", map) ;
				}
			}

		}
		
		if (msg.getTotalPrice() > 0) {
			CashLog cashLog = new CashLog();
			cashLog.setCash(totalPrice.intValue());
			cashLog.setNote("SMS 메시지 발송(" + targetList.size() + "건)");
			cashSvc.decreaseCash(msg.getAuthor(), cashLog);
			
		}

		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
	public Integer insertPushMsg(MsgOnly msg, List<PushTarget> targetList) throws ResultCodeException {
		if (targetList == null || targetList.size() == 0)
			throw new NotFoundTargetException("targetList", "empty");
		
		initMsgOnly(msg);

		msg.setType("push");
		msg.setTargetCount(targetList.size());
		msg.setPushCase(Const.USER_PUSH_PAGE);
		msg.setAppType(Const.APP_TYPE_USER);

		int price = commonSvc.getPushPrice(msg.getAuthor().getCountry());
		
		int totalPrice = msg.getTargetCount() * price;

		msg.setTotalPrice((long)totalPrice);
		if (totalPrice > 0) {
			msg.setPayType(Const.MSG_PAY_TYPE_PAY);
		} else {
			msg.setPayType(Const.MSG_PAY_TYPE_NONE);
		}

		System.out.println("/insertPushMsg : msg : " + msg.toString()) ;
		
		int effected = sqlSession.insert("Msg.insertMsg", msg);
		if (effected > 0) {

			PushTarget target = new PushTarget();
			target.setStatus("ready");
			target.setPushPrice(price);
			ParamMap map = new ParamMap() ;
			map.put("msg", msg) ;
			map.put("target", target) ;
			map.put("list",targetList);
			sqlSession.insert("Msg.insertPushTargetList", map) ; //MGK msg, target);

			map.clear() ;
			map.put("list", targetList);
			map.put("msg", msg) ;
			map.put("type", msg.getAppType()) ;
			sqlSession.insert("Msg.insertMsgBoxList", map) ;
		}
		
		if (msg.getTotalPrice() > 0) {

			CashLog cashLog = new CashLog();
			cashLog.setCash(totalPrice);
			cashLog.setNote("PUSH 발송(" + targetList.size() + "건)");
			cashSvc.decreaseCash(msg.getAuthor(), cashLog);
			
		}
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	

	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer sendNow(User user, MsgOnly msg) throws ResultCodeException {
		Msg saved = sqlSession.selectOne("Msg.getMsgOnly", msg.getNo());
		
		if (!saved.getType().equals("push") && !saved.getType().equals("sms") && !saved.getType().equals("lms")) 
			throw new InvalidArgumentException("type", "unknown type");
		
		
		if (!saved.getStatus().equals("reserved"))
			throw new InvalidArgumentException("status", "not reserved.");

		msg.setType(saved.getType());
		msg.setPushCase(Const.USER_PUSH_SENDPUSH);
		msg.setAppType(Const.APP_TYPE_USER);
		msg.setStatus(saved.getStatus());
		msg.setReserved(saved.getReserved());
		msg.setReserveDate(DateUtil.getDateAdd(DateUtil.getCurrentDate(), DateUtil.MINUTE, -1));
		int effected = sqlSession.update("Msg.updateReserveDate", msg);
		if (effected > 0)
			return Const.E_SUCCESS;
		
		return Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer cancelSend(User user, MsgOnly msg) throws ResultCodeException {
		Msg saved = sqlSession.selectOne("Msg.getMsg", msg.getNo());
		
		if (!saved.getType().equals("push") && !saved.getType().equals("sms") && !saved.getType().equals("lms")) 
			throw new InvalidArgumentException("type", "unknown type");
		
		
		if (!saved.getStatus().equals("reserved"))
			throw new InvalidArgumentException("status", "not reserved.");
		
		//메시지 발송 요금 전액 환불 처리
		long price = 0;
		if (saved.getType().equals("push")) {
			price = commonSvc.getPushPrice(saved.getAuthor().getCountry());
		} else {
			if (saved.getType().equals("sms"))
				price = commonSvc.getSmsPrice(saved.getAuthor().getCountry());
			else if (saved.getType().equals("lms"))
				price = commonSvc.getLmsPrice(saved.getAuthor().getCountry());
		}
		
		int failCount = saved.getTargetCount();
		long refundPrice = failCount * price;
		msg.setRefundPrice(refundPrice);
		if (refundPrice > 0) {
			CashLog cashLog = new CashLog();
			cashLog.setCash((int)refundPrice);
			cashLog.setNote("문자 메시지 전송 취소");
			cashSvc.increaseCash(user, cashLog);
		}
		msg.setSuccessCount(0);
		msg.setFailCount(failCount);
		msg.setStatus("cancelSend");
		return updateMsgStatus(msg);
	}
	
	
	public void setCashHistoryByMsg(MsgOnly msg, CashLog cashLog) {
		if (!StringUtils.isEmpty(msg.getSubject()))
			cashLog.setNote(msg.getSubject());
		else {
			if (msg.getType().equals("push"))
				cashLog.setNote("Push");
			else
				cashLog.setNote("문자 메시지");
		}
		
		/*
		if (history.getProperties() == null)
			history.setProperties(new HashMap<String, Object>());
		
		history.getProperties().put("msgNo", msg.getNo());
		*/
	}

	
	public int getMsgCountByTypeAndStatus(User user, MsgOnly msg) {
		SearchOpt opt = new SearchOpt();
		opt.setFilter(new ArrayList<String>());
		if (msg.getType() != null && (msg.getType().equals("sms") || msg.getType().equals("lms"))) {
			opt.getFilter().add("sms");
			opt.getFilter().add("lms");
		} else {
			opt.getFilter().add(msg.getType());
		}
		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("msg", msg) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Msg.getMsgCountByTypeAndStatus", map) ;  //MGK user, msg, opt);
	}
	
	public List<Msg> getMsgListByTypeAndStatus(User user, MsgOnly msg, SearchOpt opt) {
		opt.setFilter(new ArrayList<String>());
		if (msg.getType() != null && (msg.getType().equals("sms") || msg.getType().equals("lms"))) {
			opt.getFilter().add("sms");
			opt.getFilter().add("lms");
		} else {
			opt.getFilter().add(msg.getType());
		}
		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("msg", msg) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Msg.getMsgListByTypeAndStatus", map) ; //MGK user, msg, opt);
	}

	public List<Msg> getCompleteMsgAll() {
		return sqlSession.selectList("Msg.getCompleteMsgAll");
	}
	
	public int getMsgCountByStatus(String status) {
		return sqlSession.selectOne("Msg.getMsgCountByStatus", status);
	}
	
	public int getSmsTargetCountByStatus(MsgOnly msg, String status) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("status", status) ;
		return sqlSession.selectOne("Msg.getSmsTargetCountByStatus", map) ; //MGK msg, status);
	}
	public int getPushTargetCountByStatus(MsgOnly msg, String status) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("status", status) ;
		return sqlSession.selectOne("Msg.getPushTargetCountByStatus", map) ; //MGK msg, status);
	}

	public List<PushTarget> getPushTargetListByStatus(MsgOnly msg, String status) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("status", status) ;
		return sqlSession.selectList("Msg.getPushTargetListByStatus", map) ; //MGK msg, status);
	}

	public List<SmsTarget> getSmsTargetListByStatus(MsgOnly msg, String status) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("status", status) ;
		return sqlSession.selectList("Msg.getSmsTargetListByStatus", map) ; //MGK msg, status);
	}
	
	public boolean existsNotCompleteTarget(MsgOnly msg) {
		if (msg.getType().equals("push")) 
			return (Integer)sqlSession.selectOne("Msg.existsNotCompletePushTarget",msg) > 0 ? true : false;
		else
			return (Integer)sqlSession.selectOne("Msg.existsNotCompleteSmsTarget",msg) > 0 ? true: false;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void finishMsg(Msg msg)  {
		try {
//			if (!this.existsNotCompleteTarget(msg)) {
				logger.debug("finishMsg 스케줄러 수행 시작[");
				//성공이던 실패던 모두 전송이 완료된 경우
				int succCount = 0;
				int failCount = 0;
				long price = 0;
				if (msg.getType().equals("push")) {
					List<PushTarget> failList = getPushTargetListByStatus(msg, "fail");
					int refundPrice = 0;
					if(failList != null && failList.size() > 0){
						failCount = failList.size();
						for(PushTarget failTarget : failList){
							if(failTarget.getPushPrice() != null && failTarget.getPushPrice() > 0 ){
								refundPrice += failTarget.getPushPrice();
							}
						}
					}
					succCount = this.getPushTargetCountByStatus(msg, "success");

					msg.setRefundPrice((long)refundPrice);
					if (refundPrice > 0) {
						CashLog cashLog = new CashLog();
						cashLog.setCash(refundPrice);
						cashLog.setNote("Push 전송 실패로 인한 환불");
						cashSvc.increaseCash(msg.getAuthor(), cashLog);
					}

					msg.setStatus("finish");
					msg.setSuccessCount(succCount);
					msg.setFailCount(failCount);
					this.updateMsgStatus(msg);

				} else {

					List<SmsTarget> failList = getSmsTargetListByStatus(msg, "fail");
					int refundPrice = 0;
					if(failList != null && failList.size() > 0){
						failCount = failList.size();
						for(SmsTarget failTarget : failList){
							if(failTarget.getSmsPrice() != null && failTarget.getSmsPrice() > 0 ){
								refundPrice += failTarget.getSmsPrice();
							}
						}
					}

					succCount = this.getSmsTargetCountByStatus(msg, "success");
					failCount = msg.getTargetCount() - succCount;
					if (!Const.MSG_PAY_TYPE_NONE.equals(msg.getPayType())) {
						msg.setRefundPrice((long)refundPrice);
						if (refundPrice > 0) {
							CashLog cashLog = new CashLog();
							cashLog.setCash(refundPrice);
							cashLog.setNote("문자 전송 실패로 인한 환불");
							cashSvc.increaseCash(msg.getAuthor(), cashLog);
						}
					} else {
						msg.setRefundPrice(0L);
					}
					msg.setStatus("finish");
					msg.setSuccessCount(succCount);
					msg.setFailCount(failCount);
					this.updateMsgStatus(msg);
				}


				logger.debug("finishMsg 스케줄러 수행 종료]");
//			}
		} catch(Exception e) {
			logger.error("finishMsg : " + AppUtil.excetionToString(e)) ;
		}
	}

	public Msg getMsgWithPage(MsgOnly msg){
		return sqlSession.selectOne("Msg.getMsgWithPage", msg);
	}

	public List<MsgTarget> getMsgTargetList(MsgOnly msg){
		if (msg.getType().equals("push")){
			return sqlSession.selectList("Msg.getPushOnlyReceiverAll", msg);
		}else if (msg.getType().equals("lms") || msg.getType().equals("sms")){
			return sqlSession.selectList("Msg.getSmsOnlyReceiverAll", msg);
		}else{
			return null;
		}
	}

	public Msg getMsgWithReceiver(MsgOnly msg) {
		logger.info("getMsgWithReceiver(): msg.getType() : " + msg.getType()) ;
		if (msg.getType().equals("push"))
			return sqlSession.selectOne("Msg.getPushReceiverAll", msg);
		else if (msg.getType().equals("lms") || msg.getType().equals("sms"))
			return sqlSession.selectOne("Msg.getSmsReceiverAll", msg);
		else
			return null;
	}
	
	public int getMsgReceiverCount(MsgOnly msg, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("status", opt) ;
		
		if (msg.getType().equals("push"))
			return sqlSession.selectOne("Msg.getPushReceiverCount", map) ; //MGK msg, opt);
		else if (msg.getType().equals("lms") || msg.getType().equals("sms"))
			return sqlSession.selectOne("Msg.getSmsReceiverCount", map) ; //MGK msg, opt);
		else
			return 0;
	}

	public Msg getMsgWithReceiverList(MsgOnly msg, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("opt", opt) ;
		
		if (msg.getType().equals("push"))
			return sqlSession.selectOne("Msg.getPushReceiverList", map) ; //MGK msg, opt);
		else if (msg.getType().equals("lms") || msg.getType().equals("sms"))
			return sqlSession.selectOne("Msg.getSmsReceiverList", map) ; //MGK msg, opt);
		else
			return null;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateMsgStatus(MsgOnly msg) {
		int effected = sqlSession.update("Msg.updateMsgStatus", msg);
		if (msg.getStatus().equals("finish"))
			sqlSession.update("Msg.finishMsgProc",msg);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void updatePushTargetStatus(MsgOnly msg, PushTarget target) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("target", target) ;
		sqlSession.update("Msg.updatePushTargetStatus", map) ;
//		if (prevStatus == null)
//			return Const.E_UNKNOWN;
//
//		if (prevStatus.equals(target.getStatus()))
//			return Const.E_SAMESTATUS;
//
//		boolean validate = false;
//		if ((prevStatus.equals("ready") || prevStatus.equals("reserved")))
//			validate = true;
//		else if (prevStatus.equals("sending") && (target.getStatus().equals("success") || target.getStatus().equals("fail")))
//			validate = true;
//		else if (prevStatus.equals("fail") && target.getStatus().equals("success"))
//			validate = true;
//		else if (prevStatus.equals("success") && target.getStatus().equals("fail"))
//			return Const.E_SUCCESS; //다른 단말에 이미 성공한 경우이기 때문에 성공으로 간주한다.
//
//		if (!validate)
//			return Const.E_NOTPOSSIBLECHANGESTATUS;
//
//		int effected = sqlSession.update("Msg.updatePushTargetStatus", map) ;
//		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateSmsTargetStatus(MsgOnly msg, SmsTarget target) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("target", target) ;
		String prevStatus = sqlSession.selectOne("Msg.getSmsTargetStatus", map) ; //MGK msg, target);
		if (prevStatus == null)
			return Const.E_UNKNOWN;
		
		if (prevStatus.equals(target.getStatus()))
			return Const.E_SAMESTATUS;
		
		boolean validate = false;
		if ((prevStatus.equals("ready") || prevStatus.equals("reserved")))
			validate = true;
		else if (prevStatus.equals("sending") && (target.getStatus().equals("success") || target.getStatus().equals("fail")))
			validate = true;
		else if (prevStatus.equals("fail") && target.getStatus().equals("success"))
			validate = true;
		else if (prevStatus.equals("success") && target.getStatus().equals("fail"))
			return Const.E_SUCCESS; //다른 단말에 이미 성공한 경우이기 때문에 성공으로 간주한다.
		
		if (!validate)
			return Const.E_NOTPOSSIBLECHANGESTATUS;
		
		int effected = sqlSession.update("Msg.updateSmsTargetStatus", map) ; //MGK msg, target);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertSavedMsg(SavedMsg msg) {
		if (msg.getNo() == null)
			msg.setNo((Integer)sqlSession.selectOne("Msg.getNextSavedMsgNo",msg.getUser()));
		int effected = sqlSession.insert("Msg.insertSavedMsg", msg);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteSavedMsg(SavedMsg msg) {
		int effected = sqlSession.delete("Msg.deleteSavedMsg", msg);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}

	public int getSavedMsgCount(User user) {
		return sqlSession.selectOne("Msg.getSavedMsgCount", user);
	}
	
	public List<SavedMsg> getSavedMsgList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("opt", opt) ;
		return sqlSession.selectList("Msg.getSavedMsgList", map) ; //MGK user, opt);
	}
	
	public List<Msg> getReservedMsgAll(MsgOnly msg) {
		SearchOpt opt = new SearchOpt();
		opt.setFilter(new ArrayList<String>());
		if (msg.getType() != null && (msg.getType().equals("sms") || msg.getType().equals("lms"))) {
			opt.getFilter().add("sms");
			opt.getFilter().add("lms");
		} else {
			opt.getFilter().add(msg.getType());
		}

		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("opt", opt) ;
		return sqlSession.selectList("Msg.getReservedMsgAll", map) ;  //MGK msg, opt);
	}
	
	public List<Msg> getReservedMsgAllForSend() {
		return sqlSession.selectList("Msg.getReservedMsgAllForSend");
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void readComplete(User user, MsgOnly msg) {
		ParamMap map = new ParamMap() ;
		map.put("msg", msg);
		map.put("user", user) ;
		int exists = sqlSession.selectOne("Msg.existsNotReadPushTarget", map) ; //MGK msg, user);
		if (exists > 0) {
			int effected = sqlSession.update("Msg.readComplete", map) ;  //MGK msg, user);
			if (effected > 0) {
				sqlSession.update("Msg.increaseReadCount", msg);


				/*if (saved.getMoveType2().equals("couponDetail")) {
					BolHistory history = new BolHistory();
					history.setTargetType("msg");
					history.setTarget(msg);
					history.setAmount(10L);
					history.setSecondaryType("recvPush");
					history.setSubject("Push 수신으로 인한 적립");
					cashSvc.increaseBol(user, history);
				}*/
			}
		}
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertMsgBox(User actor, MsgOnly msg, User user, String appType) throws ResultCodeException {
		initMsgOnly(msg);
		msg.setAuthor(actor);
		msg.setTargetCount(1);
		sqlSession.insert("Msg.insertMsg", msg);

		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("msg", msg) ;
		map.put("type", appType);
		sqlSession.insert("Msg.insertMsgBox", map) ; //MGK user, msg, appType);
		
		Map<String, Object> properties = new HashMap<String, Object>();
		User saved = userSvc.getUser(user.getNo());
		
		if (msg.getType().equals("push") && msg.getStatus().equals("ready")) {
			PushTarget target = new PushTarget();
			target.setStatus("ready");
			target.setMsgType("push");
			target.setUser(user);

			map.clear() ;
			map.put("msg", msg) ;
			map.put("target", target);
			sqlSession.insert("Msg.insertPushTarget", map) ; //MGK msg, target);
		}
		
		return Const.E_SUCCESS;
	}
	
	
	public int getMsgCountInBox(User user, String appType) {
		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("type", appType);
		return sqlSession.selectOne("Msg.getMsgCountInBox", map) ; //MGK user, appType);
	}
	
	public List<Msg> getMsgListInBox(User user, SearchOpt opt, String appType) throws ResultCodeException {
		User saved = userSvc.getUser(user.getNo());
		if (saved.getProperties() != null && saved.getProperties().containsKey("newMsgCount")) {
			Integer newCount = (Integer)saved.getProperties().get("newMsgCount");
			if (newCount != null && newCount > 0) {
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("newMsgCount", 0);
				userSvc.updateProperties(saved, properties);
			}
		}

		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("opt", opt);
		map.put("type", appType);
		return sqlSession.selectList("Msg.getMsgListInBox", map) ; //MGK user, opt, appType);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteMsgInBox(User user, MsgOnly msg) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("user", user);
		map.put("msg", msg);
		int effected = sqlSession.delete("Msg.deleteMsgInBox", map) ; //MGK user, msg);
		if (effected == 0)
			throw new NotFoundTargetException();
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public int insertSKBroadbandMsg(SKBroadbandMsg msg) {
		return sqlSession.insert("Msg.insertSKBroadbandMsg", msg);
	}
	
	public SKBroadbandMsg generateSKBroadbandMsg(
			String groupKey, String sendNumber, String recvNumber
			, String title, String content, String callbackUrl, String reservedDate
			, String etc1, String etc2, String etc3, String etc4, Integer etc5, Integer etc6
			) throws UnsupportedEncodingException {
		SKBroadbandMsg msg = new SKBroadbandMsg();
		msg.setCmpMsgGroupId(groupKey);
		msg.setEtcChar1(etc1);
		msg.setEtcChar2(etc2);
		msg.setEtcChar3(etc3);
		msg.setEtcChar4(etc4);
		msg.setEtcInt5(etc5);
		msg.setEtcInt6(etc6);
		msg.setUsrId(SKBB_DBAGENTID);
		if (StringUtils.isEmpty(sendNumber))
			msg.setSndPhnId(SMS_CERT_SENDER_NUM.replaceAll("\\-", ""));
		else
			msg.setSndPhnId(sendNumber);
		msg.setRcvPhnId(recvNumber);
		
		if (!StringUtils.isEmpty(content)) {
			if (content.trim().getBytes("UTF-8").length > 90)
				msg.setUsedCd("10");
			else
				msg.setUsedCd("00");
			msg.setSndMsg(content.trim());
		}
		
		if (!StringUtils.isEmpty(callbackUrl)) {
			msg.setCallbackUrl(callbackUrl);
			if (msg.getUsedCd().startsWith("0")) {
				String totalMsg = msg.getSndMsg() + msg.getCallbackUrl();
				if (totalMsg.trim().getBytes("UTF-8").length > 90)
					msg.setUsedCd("11");
				else
					msg.setUsedCd("01");
			} else if (msg.getUsedCd().startsWith("1")) {
				msg.setUsedCd("11");
			}
		}
		
		if ("10".equals(msg.getUsedCd()) || "11".equals(msg.getUsedCd())) {
			msg.setContentCnt(1);
			msg.setContentMimeType("text/plain");
		} else {
			msg.setContentCnt(0);	
		}
		
		if (StringUtils.isEmpty(title))
			msg.setMsgTitle(StringUtils.left(msg.getSndMsg(), 50));
		else
			msg.setMsgTitle(StringUtils.left(title.trim(), 50));
		
		if (StringUtils.isEmpty(reservedDate)) {
			msg.setReservedFg("I");
			msg.setReservedDttm(DateUtil.getDateString(DateUtil.PATTERN, DateUtil.getCurrentDate()));
		} else {
			msg.setReservedFg("L");
			msg.setReservedDttm(reservedDate);
		}
		return msg;
	}
}
