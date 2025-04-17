package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.PointHistory;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.jpa.service.PointService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.RejectRequest;
import kr.co.pplus.store.type.model.Page;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.StoreUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class ExternalService extends RootService {
	private static Logger logger = LoggerFactory.getLogger(ExternalService.class);
	
//	@Autowired
//	ExternalDao dao;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	PageService pageSvc;
	
	@Autowired
	CustomerService customerSvc;
	
	@Autowired
	MsgService msgSvc;

	@Autowired
	PointService pointService;

	@Autowired
	AdpcRewardRepository adpcRewardRepository;

	@Autowired
	AdSyncRewardRepository adSyncRewardRepository;

	@Autowired
	PincruxRewardRepository pincruxRewardRepository;

	@Autowired
	BuzvilRewardRepository buzvilRewardRepository;

	@Autowired
	FlexRewardRepository flexRewardRepository;

	@Autowired
	TnkRewardRepository tnkRewardRepository;

	@Autowired
	PointClickRewardRepository pointClickRewardRepository;

	@Autowired
	SmaadRewardRepository smaadRewardRepository;
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer registRejectNumber(RejectRequest req) {
		int exists = sqlSession.selectOne("External.existsMsgRejectNumber", req);
		if (exists > 0)
			return Const.E_SUCCESS;
		

		int effected = sqlSession.insert("External.insertMsgRejectNumber", req);
			
		try {
			Page page = new Page();
			page.setNo(Long.parseLong(req.getRejectnumber()));
			
			Page savedPage = pageSvc.getPage(page);
			if (savedPage != null) {
				Customer customer = new Customer();
				customer.setPage(savedPage);
				customer.setMobile(req.getCid());
				
				Customer saved = customerSvc.getCustomerByMobile(customer);
				if (saved != null) {
					if (StoreUtil.getMask(saved.getMarketingConfig(), Const.MKT_MSG_INDEX) != false) {
						saved.setMarketingConfig(StoreUtil.changeMask(saved.getMarketingConfig(), Const.MKT_MSG_INDEX, false));
						customerSvc.updateMarketingConfig(saved);
					}
				}
			}
			
			return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return Const.E_SUCCESS;
			
	}
	
	public String callbackSendMsg(Long msgNo, String mobile, String data) {
		String[] arr = data.split("\\,");
		MsgOnly msg = new MsgOnly();
		msg.setInput("system");
		msg.setNo(msgNo);
		SmsTarget target = new SmsTarget();
		target.setMobile(mobile);
		if (arr != null && arr.length > 1) {
			int result = Integer.parseInt(arr[1]);
			if (result == 1) {
				target.setStatus("success");
			} else {
				target.setStatus("fail");
			}
		} else {
			target.setStatus("fail");
		}
		
		msgSvc.updateSmsTargetStatus(msg, target);
		return "Success";
	}

	public boolean existsFlexReward(String flexcode) {
		return ((Integer)sqlSession.selectOne("External.existsFlexReward", flexcode) > 0) ? true : false;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> rewardFlex(FlexReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			FlexRewardJpa flexRewardJpa = new FlexRewardJpa();
			flexRewardJpa.setUserkey(reward.getUserkey());
			flexRewardJpa.setFlexcode(reward.getFlexcode());
			flexRewardJpa.setPublisher_price(reward.getPublisher_price());
			flexRewardJpa.setUser_price(reward.getUser_price());
			flexRewardJpa.setAd_title(reward.getAd_title());
			flexRewardJpa.setAd_division(reward.getAd_division());
			flexRewardRepository.save(flexRewardJpa);

			if (reward.getUser_price() != null && reward.getUser_price() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getUserkey()));

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("flexReward");
//					bh.setAmount(reward.getUser_price().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getUser_price().floatValue());
					pointHistory.setSubject("무료충전소(Flex) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("return_code", "ok");

		}catch (Exception e){
			result.put("return_code", "error5");
		}

		return result;
	}

	public boolean existsTNKReward(String seq_id) {
		return ((Integer)sqlSession.selectOne("External.existsTNKReward", seq_id) > 0) ? true : false;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> rewardTNK(TNKReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			TnkRewardJpa tnkRewardJpa = new TnkRewardJpa();
			tnkRewardJpa.setSeq_id(reward.getSeq_id());
			tnkRewardJpa.setPay_pnt(reward.getPay_pnt());
			tnkRewardJpa.setMd_user_nm(reward.getMd_user_nm());
			tnkRewardJpa.setApp_id(reward.getApp_id());
			tnkRewardJpa.setPay_dt(reward.getPay_dt());
			tnkRewardJpa.setApp_nm(reward.getApp_nm());
			tnkRewardRepository.save(tnkRewardJpa);

			if (reward.getPay_pnt() != null && reward.getPay_pnt() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getMd_user_nm()));

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("tnkReward");
//					bh.setAmount(reward.getPay_pnt().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getPay_pnt().floatValue());
					pointHistory.setSubject("무료충전소(TNK) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("return_code", "ok");

		}catch (Exception e){
			result.put("return_code", "error");
		}

		return result;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void rewardPointClick(PointClickReward reward) {

		boolean exist = pointClickRewardRepository.existsByTransactionKey(reward.getTransactionKey());
		if(!exist){
			reward = pointClickRewardRepository.save(reward);

			if (reward.getPoint() != null && reward.getPoint() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getPickerUid()));

				if(user != null){

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getPoint());
					pointHistory.setSubject("포인트클릭 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}
		}
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public void rewardSmaadReward(SmaadReward reward) {


		boolean exist = false;
		if(!AppUtil.isEmpty(reward.getOrdersId())){
			exist = smaadRewardRepository.existsByOrdersId(reward.getOrdersId());
		}else if(!AppUtil.isEmpty(reward.getInstallId())){
			exist = smaadRewardRepository.existsByOrdersId(reward.getInstallId());
		}

		if(!exist){
			if(reward.getApproved().equals(1)){
				reward = smaadRewardRepository.save(reward);

				if (reward.getUserPay2() != null && reward.getUserPay2() > 0) {
					User user = userSvc.getUser(Long.valueOf(reward.getUser()));

					if(user != null){

						PointHistory pointHistory = new PointHistory();
						pointHistory.setMemberSeqNo(user.getNo());
						pointHistory.setType("charge");
						pointHistory.setPoint(reward.getUserPay2().floatValue());
						pointHistory.setSubject("Sma Ad 적립");
						pointService.updatePoint(user.getNo(), pointHistory);
					}

				}
			}

		}
	}

	public boolean existsBuzvil(String transaction_id) {
		return ((Integer)sqlSession.selectOne("External.existsBuzvil", transaction_id) > 0) ? true : false;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> rewardBuzvil(BuzvilReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			BuzvilRewardJpa buzvilRewardJpa = new BuzvilRewardJpa();
			buzvilRewardJpa.setUnit_id(reward.getUnit_id());
			buzvilRewardJpa.setTransaction_id(reward.getTransaction_id());
			buzvilRewardJpa.setUser_id(reward.getUser_id());
			buzvilRewardJpa.setCampaign_id(reward.getCampaign_id());
			buzvilRewardJpa.setCampaign_name(reward.getCampaign_name());
			buzvilRewardJpa.setTitle(reward.getTitle());
			buzvilRewardJpa.setPoint(reward.getPoint());
			buzvilRewardJpa.setBase_point(reward.getBase_point());
			buzvilRewardJpa.setIs_media(reward.getIs_media());
			buzvilRewardJpa.setRevenue_type(reward.getRevenue_type());
			buzvilRewardJpa.setAction_type(reward.getAction_type());
			buzvilRewardJpa.setEvent_at(reward.getEvent_at());
			buzvilRewardJpa.setExtra(reward.getExtra());
			buzvilRewardJpa.setUnit_price(reward.getUnit_price());
			buzvilRewardJpa.setCustom(reward.getCustom());
			buzvilRewardJpa.setIfa(reward.getIfa());
			buzvilRewardJpa.setReward(reward.getReward());
			buzvilRewardJpa.setAllow_multiple_conversions(reward.getAllow_multiple_conversions());
			buzvilRewardRepository.save(buzvilRewardJpa);

			if (reward.getPoint() != null && reward.getPoint() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getUser_id()));

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("buzvilReward");
//					bh.setAmount(reward.getPoint().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getPoint().floatValue());
					pointHistory.setSubject("무료충전소(버즈빌) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("result", "ok");

		}catch (Exception e){
			result.put("result", "fail");
		}

		return result;
	}

	public boolean existsPincruxReward(String transid) {
		return ((Integer)sqlSession.selectOne("External.existsPincruxReward", transid) > 0) ? true : false;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> rewardPincrux(PincruxReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			PincruxRewardJpa pincruxRewardJpa = new PincruxRewardJpa();
			pincruxRewardJpa.setAppkey(reward.getAppkey());
			pincruxRewardJpa.setPubkey(reward.getPubkey());
			pincruxRewardJpa.setUsrkey(reward.getUsrkey());
			pincruxRewardJpa.setApp_title(reward.getApp_title());
			pincruxRewardJpa.setCoin(reward.getCoin());
			pincruxRewardJpa.setTransid(reward.getTransid());
			pincruxRewardJpa.setResign_flag(reward.getResign_flag());
			pincruxRewardRepository.save(pincruxRewardJpa);

			if (reward.getCoin() != null && reward.getCoin() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getUsrkey()));

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("pincruxReward");
//					bh.setAmount(reward.getCoin().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getCoin().floatValue());
					pointHistory.setSubject("무료충전소(핀크럭스) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("code", "00");

		}catch (Exception e){
			result.put("code", "99");
		}

		return result;
	}

	public boolean existsAdSyncReward(String seq_id) {
		return ((Integer)sqlSession.selectOne("External.existsAdSyncReward", seq_id) > 0) ? true : false;
	}

	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> rewardAdSync(AdSyncReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();


		try {
			AdSyncRewardJpa adSyncRewardJpa = new AdSyncRewardJpa();
			adSyncRewardJpa.setPartner(reward.getPartner());
			adSyncRewardJpa.setCust_id(reward.getCust_id());
			adSyncRewardJpa.setAd_no(reward.getAd_no());
			adSyncRewardJpa.setSeq_id(reward.getSeq_id());
			adSyncRewardJpa.setPoint(reward.getPoint());
			adSyncRewardJpa.setAd_title(reward.getAd_title());
			adSyncRewardRepository.save(adSyncRewardJpa);

			if (reward.getPoint() != null && reward.getPoint() > 0) {
				User user = userSvc.getUser(Long.valueOf(reward.getCust_id()));

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("adSyncReward");
//					bh.setAmount(reward.getPoint().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getPoint().floatValue());
					pointHistory.setSubject("무료충전소(애드싱크) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("Result", true);
			result.put("ResultCode", 1);
			result.put("ResultMsg", "success");

		}catch (Exception e){
			result.put("Result", false);
			result.put("ResultCode", 5);
			result.put("ResultMsg", "Unknown");
		}
//		int effected = sqlSession.insert("External.insertAdSyncReward", reward);

		return result;
	}
	
	public boolean existsAdpopcornReward(String rewardKey) {
		return ((Integer)sqlSession.selectOne("External.existsAdpopcornReward", rewardKey) > 0) ? true : false;
	}

	
	@Transactional(transactionManager="jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public Map<String,Object> reward(AdpopcornReward reward) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			AdpcRewardJpa adpcReward = new AdpcRewardJpa();
			adpcReward.setReward_key(reward.getReward_key());
			adpcReward.setMember_seq_no(reward.getUsn());
			adpcReward.setQuantity(reward.getQuantity());
			adpcReward.setCampaign_key(reward.getCampaign_key());
			adpcReward.setReg_datetime(AppUtil.localDatetimeNowString());
			adpcReward = adpcRewardRepository.save(adpcReward);

			if (reward.getQuantity() != null && reward.getQuantity() > 0) {
				User user = userSvc.getUser(reward.getUsn());

				if(user != null){
//					BolHistory bh = new BolHistory();
//					bh.setSecondaryType("adpcReward");
//					bh.setAmount(reward.getQuantity().floatValue());
//					bh.setTarget(user);
//					bh.setTargetType("member");
//					bh.setSubject("무료충전소 적립");
//					bh.setProperties(new HashMap<String, Object>());
//					//bh.getProperties().put("리워드 식별값", reward.getReward_key());
//					bh.getProperties().put("적립유형", "럭키볼 충전소");
//					bh.getProperties().put("지급처", "캐시픽 운영팀");
//
//					cashBolSvc.increaseBol(user, bh);

					PointHistory pointHistory = new PointHistory();
					pointHistory.setMemberSeqNo(user.getNo());
					pointHistory.setType("charge");
					pointHistory.setPoint(reward.getQuantity().floatValue());
					pointHistory.setSubject("무료충전소(애드팝콘) 적립");
					pointService.updatePoint(user.getNo(), pointHistory);
				}

			}

			result.put("Result", true);
			result.put("ResultCode", 1);
			result.put("ResultMsg", "success");

		}catch (Exception e){
			result.put("Result", false);
			result.put("ResultCode", 4000);
			result.put("ResultMsg", "Unknown");
		}


		return result;
	}
}
