package kr.co.pplus.store.api.jpa.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.AlreadyLimitException;
import kr.co.pplus.store.exception.NotMatchedValueException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class PageAttendanceService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(PageAttendanceService.class);

	@Autowired
	PageAttendanceRepository pageAttendanceRepository;

	@Autowired
	PageAttendanceLogRepository pageAttendanceLogRepository;

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

	public PageAttendance saveAndGet(Long memberSeqNo, Long pageSeqNo){

		PageAttendance pageAttendance = pageAttendanceRepository.findFirstByPageSeqNoAndMemberSeqNoAndStatusOrderBySeqNoDesc(pageSeqNo, memberSeqNo, 1);
		if(pageAttendance == null){

            Page page = pageRepository.findBySeqNo(pageSeqNo);

			pageAttendance = new PageAttendance();
			pageAttendance.setAttendanceCount(0);
			if(page.getVisitCount() != null && page.getVisitCount() > 0){
                pageAttendance.setTotalCount(page.getVisitCount());
            }else{
                pageAttendance.setTotalCount(10);
            }

			pageAttendance.setMemberSeqNo(memberSeqNo);
			pageAttendance.setPageSeqNo(pageSeqNo);
			pageAttendance.setStatus(1);
			pageAttendance.setRegDatetime(AppUtil.localDatetimeNowString());
			pageAttendance = pageAttendanceRepository.saveAndFlush(pageAttendance);
		}

		return pageAttendance;
	}

	public PageAttendance getBySeqNo(Long seqNo){
		return pageAttendanceRepository.findBySeqNo(seqNo);
	}

	public List<PageAttendanceLog> getPageAttendanceLogList(Long pageAttendanceSeqNo){
		return  pageAttendanceLogRepository.findAllByPageAttendanceSeqNoOrderBySeqNoAsc(pageAttendanceSeqNo);
	}

	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public PageAttendance attendanceWithStamp(User user, Long pageAttendanceSeqNo, Long pageSeqNo, String token) throws ResultCodeException {

		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(pageSeqNo);

		PageAttendance pageAttendance = pageAttendanceRepository.findBySeqNo(pageAttendanceSeqNo);

		if(!page.getSeqNo().equals(pageAttendance.getPageSeqNo())){
			throw new NotPermissionException();
		}

		if(pageAttendance.getStatus() != 1 || pageAttendance.getAttendanceCount() >= pageAttendance.getTotalCount()){
			throw new AlreadyLimitException();
		}

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

		String dateStr = AppUtil.localDatetimeNowString();

		PageAttendanceLog pageAttendanceLog = new PageAttendanceLog();
		pageAttendanceLog.setMemberSeqNo(user.getNo());
		pageAttendanceLog.setPageAttendanceSeqNo(pageAttendanceSeqNo);
		pageAttendanceLog.setRegDatetime(dateStr);
		pageAttendanceLog = pageAttendanceLogRepository.saveAndFlush(pageAttendanceLog);

		pageAttendance.setAttendanceCount(pageAttendance.getAttendanceCount()+1);
		if(pageAttendance.getAttendanceCount() >= pageAttendance.getTotalCount()){
			pageAttendance.setStatus(2);
		}
		pageAttendance = pageAttendanceRepository.saveAndFlush(pageAttendance);

		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
		pageAdvertiseHistory.setPageSeqNo(pageSeqNo);
		pageAdvertiseHistory.setPageAttendanceSeqNo(pageAttendance.getSeqNo());
		pageAdvertiseHistory.setType("visit");
		pageAdvertiseHistory.setRegDatetime(dateStr);

		if(page.getVisitPointFee() != null){
			pageAdvertiseHistory.setPrice(page.getVisitPointFee());
		}else{
			pageAdvertiseHistory.setPrice(Const.ADS_COST);
		}

		if(pageAttendance.getAttendanceCount() >= pageAttendance.getTotalCount()){

			VisitorPointGiveHistory visitorPointGiveHistory = new VisitorPointGiveHistory();
			visitorPointGiveHistory.setSenderSeqNo(page.getMemberSeqNo());
			visitorPointGiveHistory.setPageSeqNo(pageSeqNo);
			visitorPointGiveHistory.setReceiverSeqNo(user.getNo());
			visitorPointGiveHistory.setPrice(page.getVisitPoint());
			visitorPointGiveHistory.setType("visit");
			visitorPointGiveHistory.setIsPayment(false);
			visitorPointGiveHistory.setRegDatetime(dateStr);
			visitorPointGiveHistory = visitorPointGiveHistoryRepository.saveAndFlush(visitorPointGiveHistory);
			pageAdvertiseHistory.setVisitorPointGiveHistorySeqNo(visitorPointGiveHistory.getSeqNo());

			BolHistory bolHistory = new BolHistory();
			bolHistory.setAmount(page.getVisitPoint().floatValue());
			bolHistory.setPageSeqNo(pageSeqNo);
			bolHistory.setMemberSeqNo(user.getNo());

			bolHistory.setPrimaryType("increase");

			bolHistory.setTargetType("member");
			bolHistory.setTargetSeqNo(user.getNo());
			bolHistory.setHistoryProp(new HashMap<String, Object>());
			bolHistory.getHistoryProp().put("지급처", page.getName());
			bolHistory.setSecondaryType("visit");
			bolHistory.setSubject("방문 적립");
			bolHistory.getHistoryProp().put("적립유형", "방문 적립");

			bolService.increaseBol(user.getNo(), bolHistory);
		}


		pageAdvertiseHistoryService.save(pageAdvertiseHistory, user.getNo());

		Plus plus = new Plus();
		plus.setMemberSeqNo(user.getNo());
		plus.setPageSeqNo(pageSeqNo);
		plus.setAgreement(false);
		plus.setBuyCount(0);
		plus.setBlock(false);
		plus.setPlusGiftReceived(false);
		plus.setPushActivate(true);
		plus.setRegDatetime(AppUtil.localDatetimeNowString());

		plusJpaService.insertPlus(plus);

		return pageAttendance;
	}


	@Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
	public PageAttendance attendanceFromPage(User user, Long pageAttendanceSeqNo, Long receiverSeqNo, Long pageSeqNo) throws ResultCodeException {

		kr.co.pplus.store.api.jpa.model.Page page = pageRepository.findBySeqNo(pageSeqNo);

		if (!user.getNo().equals(page.getMemberSeqNo())){
			throw new NotPermissionException("user", "not page owner");
		}

		PageAttendance pageAttendance = pageAttendanceRepository.findBySeqNo(pageAttendanceSeqNo);


		String dateStr = AppUtil.localDatetimeNowString();

		PageAttendanceLog pageAttendanceLog = new PageAttendanceLog();
		pageAttendanceLog.setMemberSeqNo(receiverSeqNo);
		pageAttendanceLog.setPageAttendanceSeqNo(pageAttendanceSeqNo);
		pageAttendanceLog.setRegDatetime(dateStr);
		pageAttendanceLog = pageAttendanceLogRepository.saveAndFlush(pageAttendanceLog);

		pageAttendance.setAttendanceCount(pageAttendance.getAttendanceCount()+1);
		if(pageAttendance.getAttendanceCount() >= pageAttendance.getTotalCount()){
			pageAttendance.setStatus(2);
		}
		pageAttendance = pageAttendanceRepository.saveAndFlush(pageAttendance);

		PageAdvertiseHistory pageAdvertiseHistory = new PageAdvertiseHistory();
		pageAdvertiseHistory.setPageSeqNo(pageSeqNo);
		pageAdvertiseHistory.setPageAttendanceSeqNo(pageAttendance.getSeqNo());
		pageAdvertiseHistory.setType("visit");
		pageAdvertiseHistory.setRegDatetime(dateStr);

		if(page.getVisitPointFee() != null){
			pageAdvertiseHistory.setPrice(page.getVisitPointFee());
		}else{
			pageAdvertiseHistory.setPrice(Const.ADS_COST);
		}

		if(pageAttendance.getAttendanceCount() >= pageAttendance.getTotalCount()){

			VisitorPointGiveHistory visitorPointGiveHistory = new VisitorPointGiveHistory();
			visitorPointGiveHistory.setSenderSeqNo(page.getMemberSeqNo());
			visitorPointGiveHistory.setPageSeqNo(pageSeqNo);
			visitorPointGiveHistory.setReceiverSeqNo(receiverSeqNo);
			visitorPointGiveHistory.setPrice(page.getVisitPoint());
			visitorPointGiveHistory.setType("visit");
			visitorPointGiveHistory.setIsPayment(false);
			visitorPointGiveHistory.setRegDatetime(dateStr);
			visitorPointGiveHistory = visitorPointGiveHistoryRepository.saveAndFlush(visitorPointGiveHistory);
			pageAdvertiseHistory.setVisitorPointGiveHistorySeqNo(visitorPointGiveHistory.getSeqNo());

			BolHistory bolHistory = new BolHistory();
			bolHistory.setAmount(page.getVisitPoint().floatValue());
			bolHistory.setPageSeqNo(pageSeqNo);
			bolHistory.setMemberSeqNo(receiverSeqNo);

			bolHistory.setPrimaryType("increase");

			bolHistory.setTargetType("member");
			bolHistory.setTargetSeqNo(receiverSeqNo);
			bolHistory.setHistoryProp(new HashMap<String, Object>());
			bolHistory.getHistoryProp().put("지급처", page.getName());
			bolHistory.setSecondaryType("visit");
			bolHistory.setSubject("방문 적립");
			bolHistory.getHistoryProp().put("적립유형", "방문 적립");

			bolService.increaseBol(receiverSeqNo, bolHistory);

			try {
				MsgJpa msgJpa = new MsgJpa();
				msgJpa.setSeqNo(null);
				msgJpa.setIncludeMe(false);
				msgJpa.setInputType(Const.MSG_INPUT_SYSTEM);
				msgJpa.setStatus(Const.MSG_STATUS_READY);
				msgJpa.setMsgType(Const.MSG_TYPE_PUSH);
				msgJpa.setMoveType1(Const.MOVE_TYPE_INNER);
				msgJpa.setMoveType2(Const.MOVE_TYPE_BOLHISTORY);
				msgJpa.setSubject("캐시가 지급되었습니다.");
				msgJpa.setContents("방문 적립 : "+visitorPointGiveHistory.getPrice()+"원");

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
			}catch (Exception e){
				logger.error(e.toString());
			}
		}


		pageAdvertiseHistoryService.save(pageAdvertiseHistory, receiverSeqNo);

		Plus plus = new Plus();
		plus.setMemberSeqNo(receiverSeqNo);
		plus.setPageSeqNo(pageSeqNo);
		plus.setAgreement(false);
		plus.setBuyCount(0);
		plus.setBlock(false);
		plus.setPlusGiftReceived(false);
		plus.setPushActivate(true);
		plus.setRegDatetime(AppUtil.localDatetimeNowString());

		plusJpaService.insertPlus(plus);

		return pageAttendance;
	}
}
