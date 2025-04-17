package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.queue.Firebase;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class QueueService {

	private final static Logger logger = LoggerFactory.getLogger(QueueService.class);

	@Autowired
    MsgProducer producer;
	
	@Autowired
	MsgService msgSvc;
	
	@Autowired
	CouponService couponSvc;
	
	@Autowired
	NoteService noteSvc;
	
	@Autowired
	PageService pageSvc;
	
	@Autowired
	PlusService plusSvc;
	
	@Autowired
	EventService eventSvc;

	@Autowired
	UserService userSvc ;

//	@Autowired
//	BuyGoodsWithDateRepository buyGoodsWithDateRepository ;
//
//	@Autowired
//	GoodsWithDateRepository goodsWithDateRepository ;
//
//	@Autowired
//	GoodsDetailRepository goodsDetailRepository ;
//
//	@Autowired
//	BuyCallbackRepository buyCallbackRepository ;
//
//	@Autowired
//	BuyRepository buyRepository ;
//
//    @Autowired
//    BuyWithDateRepository buyWithDateRepository ;
//
//    @Autowired
//    GoodsRepository goodsRepository ;
//
//    @Autowired
//	GoodsLikeRepository goodsLikeRepository;
//
//    @Autowired
//    BuyGoodsRepository buyGoodsRepository ;

	@Autowired
	CashBolService cashBolSvc;

	@Autowired
	UserService userService;

//	@Autowired
//	GoodsService goodsService;

	@Value("${STORE.TYPE}")
	String storeType ;

    @Value("${STORE.ORDER_WAIT_TIME}")
	long orderWaitTime = 5 ; // 5mins


	public void sendPush(Long memberSeqNo, Map<String, String> data, String appType){

		try {
			User user = new User();
			user.setNo(memberSeqNo);
			List<UserApp> appList = userService.getUserApp(user);

			if( appList.size() > 0 ) {
				Firebase firebase = null;
				if(appType.equals(Const.APP_TYPE_LUCKYBOL)) {
					firebase = Firebase.getLuckyBolInstance();
				}else if(appType.equals(Const.APP_TYPE_ORDER)){
					firebase = Firebase.getOrderInstance() ;
				}else{
					firebase = Firebase.getUserInstance() ;
				}

				logger.info("Firebase.getUserInstance() : " + firebase.toString());
				firebase.sendMulticastPush(data, appList, new Firebase.PushResultHandler() {
					@Override
					public void error(Object arg, User user, String cause) {
					}

					@Override
					public void send(Object arg, User user) {
					}

					@Override
					public void notFoundPushKey(String pushKey) {
						if(StringUtils.isNotEmpty(pushKey)){
							userService.deleteInstalledAppByPushKey(pushKey);
						}
					}
				}, null);
			}
		}catch (Exception e){
			logger.error(e.toString());
		}

	}

	public void sendPushList(List<Long> memberSeqNoList, Map<String, String> data, String appType){

		try {
			for(Long memberSeqNo : memberSeqNoList){
				User user = new User();
				user.setNo(memberSeqNo);
				List<UserApp> appList = userService.getUserApp(user);

				if( appList.size() > 0 ) {
					Firebase firebase = null;
					if(appType.equals(Const.APP_TYPE_LUCKYBOL)){
						firebase = Firebase.getLuckyBolInstance() ;
					}else{
						firebase = Firebase.getUserInstance() ;
					}

					logger.info("Firebase.getUserInstance() : " + firebase.toString());
					firebase.sendMulticastPush(data, appList, new Firebase.PushResultHandler() {
						@Override
						public void error(Object arg, User user, String cause) {
						}

						@Override
						public void send(Object arg, User user) {
						}

						@Override
						public void notFoundPushKey(String pushKey) {
							if(StringUtils.isNotEmpty(pushKey)){
								userService.deleteInstalledAppByPushKey(pushKey);
							}
						}
					}, null);
				}
			}

		}catch (Exception e){
			logger.error(e.toString());
		}

	}

	public void sendOnlyPush(MsgOnly msg){
		msgSvc.initMsgOnly(msg);
		if (msg.getType().equals("push") && msg.getStatus().equals("ready")) {
			producer.push(msg);
		}
	}

	public void insertMsgBox(User actor, MsgOnly msg, User user, String appType) throws ResultCodeException {
		msgSvc.insertMsgBox(actor, msg, user, appType);

		if (msg.getType().equals("push") && msg.getStatus().equals("ready")) {
			producer.push(msg);
		}


//		try {
//			if(msg.getType().equals("push") && msg.getStatus().equals("ready")){
//				String url = "";
//
//				if(storeType.equals("PROD")){
//					url = adminProdUrl + "/push/api/send";
//				}else{
//					url = adminStageUrl + "/push/api/send";
//				}
//
//				List<NameValuePair> nameValuePairList = new ArrayList<>();
////				nameValuePairList.add(new BasicNameValuePair("pageSeqNo", user.getNo().toString()));
//				nameValuePairList.add(new BasicNameValuePair("memberSeqNo", user.getNo().toString()));
//				nameValuePairList.add(new BasicNameValuePair("subject", msg.getSubject()));
//				nameValuePairList.add(new BasicNameValuePair("content", msg.getContents()));
//				nameValuePairList.add(new BasicNameValuePair("moveType1", msg.getMoveType1()));
//				nameValuePairList.add(new BasicNameValuePair("moveType2", msg.getMoveType2()));
//				nameValuePairList.add(new BasicNameValuePair("appType", appType));
//				nameValuePairList.add(new BasicNameValuePair("moveTarget", msg.getMoveTarget().getNo().toString()));
//
//				CloseableHttpClient client = HttpClients.createDefault();
//				HttpPost post = AppUtil.getPost(url, nameValuePairList) ;
//				HttpResponse res = client.execute(post) ;
////				client.execute(post) ;
//				Gson gson = new Gson() ;
//				PushSendResponse pushSendResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), PushSendResponse.class) ;
//				logger.debug("PushSendResponse: " + gson.toJson(pushSendResponse)) ;
//				client.close() ;
//			}
//		}catch (Exception e){
//
//		}


	}
	
	public Integer insertPushMsg(MsgOnly msg, List<PushTarget> targetList) throws ResultCodeException {
		Integer r = msgSvc.insertPushMsg(msg, targetList);
		if (r.equals(Const.E_SUCCESS))
			producer.push(msg);
		return r;
	}
	
	public Integer insertSmsMsg(MsgOnly msg, List<SmsTarget> targetList) throws ResultCodeException {
		Integer r = msgSvc.insertSmsMsg(msg, targetList);
		if (r.equals(Const.E_SUCCESS))
			producer.push(msg);
		return r;
	}	
	
	
	public Integer requestCouponUse(User user, Coupon coupon) throws ResultCodeException {
		Integer result = couponSvc.requestUse(user, coupon);
		if (Const.E_SUCCESS.equals(result))
			producer.push(coupon);
		return result;
	}

	public Integer confirmCouponUse(User user, Coupon coupon) throws ResultCodeException {
		Integer result = couponSvc.confirmUse(user, coupon);
		if (Const.E_SUCCESS.equals(result))
			producer.push(coupon);
		return result;
	}
	
	public Integer insertNote(User user, Note note) throws ResultCodeException {
		Integer result = noteSvc.insert(user, note);
		if (Const.E_SUCCESS.equals(result))
			producer.push(note);
		
		return result;

	}

	public Integer replyNote(User user, Note origin, Note note) throws ResultCodeException {
		Integer result = noteSvc.reply(user, origin, note);
		if (Const.E_SUCCESS.equals(result))
			producer.push(note);
		return result;
	}
	
	public Integer insertPlus(User user, Plus plus) throws ResultCodeException {

		if(plus.getAgreement() == null){
			plus.setAgreement(false);
		}

		Integer result = plusSvc.insert(user, plus);
		if (Const.E_SUCCESS.equals(result)) {
			Page page = pageSvc.getPageWithUser(plus);
			
			MsgOnly msg = new MsgOnly();
			msg.setInput("system");
			msg.setStatus("ready");
			msg.setType("push");
			msg.setMoveType1("inner");
			msg.setMoveType2("plus");
			msg.setMoveTarget(user);
			msg.setSubject(user.getDisplayName() + "님이 회원님의 샵을 플러스 하셨습니다.");
			msg.setContents(msg.getSubject());
			msg.setPushCase(Const.BIZ_PUSH_PLUSME);
			msg.setAppType(Const.APP_TYPE_BIZ);
			insertMsgBox(user, msg, page.getUser(), Const.APP_TYPE_BIZ);
		}
		return result;
	}

//	public void checkExpiredBuyGoods() {
//
//		try {
//			String dateStr = AppUtil.localDatetimeNowString() ;
//			List<BuyGoods> list = buyGoodsRepository.findAllExpiredBuyGoodsList() ;
//
//
//			Integer process = BuyProcess.EXPIRED.getProcess() ;
//			String memo = "사용처리 유효기간이 만료되어 결제를 취소 합니다." ;
//			if( list != null ){
//				List<Long> buySeqNoList = new ArrayList<>();
//				for(BuyGoods buyGoods : list){
//
//					logger.debug("buyGoodsWithDate title : " + buyGoods.getTitle() + " "+ buyGoods.getExpireDatetime());
//
//					logger.debug("eheckExpiredBuyGoods() : select buyGoodsWithDate : " + buyGoods.getPayDatetime()+ ":" + buyGoods.getModDatetime()+ ":" + buyGoods.getUseDatetime()) ;
//					buyGoods.setMemo(memo) ;
//					buyGoods.setModDatetime(dateStr);
//					buyGoods.setProcess(process);
//
//                    buyGoods = buyGoodsRepository.saveAndFlush(buyGoods);
//                    if(!buySeqNoList.contains(buyGoods.getBuySeqNo())){
//						buySeqNoList.add(buyGoods.getBuySeqNo());
//					}
//				}
//
//				for(Long seqNo : buySeqNoList){
//					buyRepository.updateExpiredBySeqNo(process, seqNo, dateStr);
//				}
//			}
//		}
//		catch(Exception e){
//			logger.error(AppUtil.excetionToString(e)) ;
//		}
//	}

//	@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
//	public void paymentPoint(){
//		List<Buy> buyList = buyRepository.findAllByIsPaymentPoint(false);
//		String dateStr = AppUtil.localDatetimeNowString();
//		for(Buy buy : buyList){
//			Integer point = buy.getSavedPoint();
//			if (point != null && point > 0) {
//				User user = new User(buy.getMemberSeqNo());
//				BolHistory history = new BolHistory();
//				history.setAmount((long)point);
//				history.setUser(user);
//				history.setSubject("상품구매 적립");
//				history.setPrimaryType("increase");
//				history.setSecondaryType("buy");
//				history.setTargetType("member");
//				history.setTarget(user);
//				history.setProperties(new HashMap<String, Object>());
//				history.getProperties().put("지급처", "오리마켓 운영팀");
//				if(buy.getOrderType() != null && buy.getOrderType() == 4){
//					history.getProperties().put("적립유형", "방문결제완료");
//				}else{
//					history.getProperties().put("적립유형", buy.getTitle());
//				}
//
//				cashBolSvc.increaseBol(user, history);
//			}
//
//			buyGoodsRepository.updateBuyGoodsPaymentPointByBuySeqNo(buy.getSeqNo(), dateStr, 1);
//			buy.setIsPaymentPoint(true);
//			buyRepository.saveAndFlush(buy);
//		}
//
//	}

//	public Map<Long, Integer> checkExpiredBuyByOrderProcess() {
//
//		Map<Long, Integer> buyCancelMap = new HashMap<Long, Integer>() ;
//		try {
//
//            Date now = Date.from(AppUtil.localDatetimeNowPlusMin(-orderWaitTime).toInstant()) ;
//			List<BuyWithDate> list = buyWithDateRepository.findAllByOrderProcessAndOrderDatetimeLessThan(OrderProcess.WAIT.getProcess(), now) ;
//
//			Integer process = BuyProcess.BIZ_CANCEL.getProcess() ;
//			String memo = "오더 주문확인 시간(" + orderWaitTime + "분)이 지나서 자동으로 결제를 취소 합니다." ;
//			if( list != null ){
//				for(BuyWithDate buyWithDate : list){
//
//                    buyCancelMap.put(buyWithDate.getSeqNo(), 1) ;
//
//                    buyWithDate.setCancelDatetime(Date.from(AppUtil.localDatetimeNow(0).toInstant()));
//
//					MsgForPush msg = new MsgForPush() ;
//					msg.setInput(Const.MSG_INPUT_SYSTEM);
//					msg.setStatus(Const.MSG_STATUS_READY);
//					msg.setType(Const.MSG_TYPE_PUSH);
//					msg.setMoveType1(Const.MOVE_TYPE_INNER);
//					msg.setMoveTarget(new NoOnlyKey(buyWithDate.getSeqNo()));
//					msg.setPushCase(Const.USER_PUSH_SENDPUSH);
//					msg.setAppType(Const.APP_TYPE_USER);
//					Goods goods = null ;
//
//					String nowStr = AppUtil.localDatetimeNowString() ;
//					for(BuyGoods buyGoods : buyWithDate.getBuyGoodsList()) {
//
//                        if (goods == null) {
//                            goods = goodsRepository.findBySeqNo(buyGoods.getGoodsSeqNo());
//                        }
//
//                        buyGoods.setCancelDatetime(nowStr);
//                        buyGoods.setModDatetime(nowStr);
//                        buyGoods.setOrderProcess(OrderProcess.REFUND.getProcess());
//                        buyGoods.setProcess(process);
//                        buyGoods = buyGoodsRepository.saveAndFlush(buyGoods) ;
//                    }
//					Object objs1[] = {goods.getName()} ;
//					Object objs2[] = {goods.getCount()} ;
//					//ToDo 메세지 적용이 안됨 ???
//					//            msg.setContents(message.getMessage("buyGoods.process.name", objs1) + "," +
//					//                            message.getMessage("buyGoods.process.count", objs2));
//
//
//					buyWithDate.setModDatetime(buyWithDate.getCancelDatetime());
//					buyWithDate.setProcess(process);
//					buyWithDate.setOrderProcess(OrderProcess.REFUND.getProcess());
//					msg.setSubject("구매취소") ;
//					msg.setContents("구매하신 상품의 상점주 주문 확인 시간(" + orderWaitTime + "분)이 지나서 구매가 자동으로 취소되었습니다.") ;
//					msg.setMoveType2(Const.MOVE_TYPE_ORDER_CANCEL) ;
//
//					if( memo != null ) {
//                        buyWithDate.setMemo(memo);
//                    }
//					else if( buyWithDate.getMemo() == null ) {
//                        buyWithDate.setMemo("");
//                    }
//
//					buyWithDate = buyWithDateRepository.saveAndFlush(buyWithDate);
//
//					User user = userSvc.getUser(buyWithDate.getMemberSeqNo()) ;
//					this.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_USER);
////					user = userSvc.getUser(goodsDetail.getPage().getMemberSeqNo()) ;
////					msg.setSubject(msg.getSubject().replaceAll("님 : ", "님이 ")) ;
////					this.insertMsgBox(StoreUtil.getCommonAdmin(), msg, user, Const.APP_TYPE_BIZ);
//					logger.debug("eheckExpiredBuyOrderProcess() : " + buyWithDate.toString());
//				}
//			}
//
//		}
//		catch(Exception e){
//			logger.error(AppUtil.excetionToString(e)) ;
//		} finally {
//			return buyCancelMap ;
//		}
//	}

//	public void checkExpired(){
//
//        checkExpiredGoods();
//		checkExpiredBuyGoods();
//
//
////        Set<Map.Entry<Long,Integer>> set = map.entrySet() ;
////        for(Map.Entry<Long,Integer> entry : set) {
////            Long buySeqNo = entry.getKey() ;
////            bootPayCancel(buySeqNo, entry.getValue()) ;
////        }
//    }

//	public void checkExpiredGoods() {
//		try {
//
////			goodsWithDateRepository.updateExpiredGoods() ;
//			List<GoodsWithDate> goodsList = goodsWithDateRepository.findAllExpireGoods();
//			for(GoodsWithDate goodsWithDate : goodsList){
//                goodsWithDateRepository.updateGoodsStatusByGoodsSeqNo(goodsWithDate.getSeqNo(), GoodsStatus.EXPIRE.getStatus());
//				goodsService.updateGoodsPriceStatusByGoodsSeqNo(goodsWithDate.getSeqNo(), GoodsStatus.EXPIRE.getStatus());
//			}
//
//			goodsLikeRepository.deleteExpiredGoods();
//
//		}
//		catch(Exception e){
//			logger.error(AppUtil.excetionToString(e)) ;
//		}
//	}

//	public void bootPayCancel(Long seqNo, Integer status) {
//		try {
//			Buy buy = buyRepository.findBySeqNo(seqNo);
//			String dateStr = AppUtil.localDatetimeNowString();
////			if(buy.getIsPlus() || buy.getIsHotdeal()){
////				if (buy.getType().intValue() == GoodsType.MENU_GOODS.getType())
////					buy.setOrderProcess(OrderProcess.CANCEL.getProcess());
////
////				buy.setProcess(BuyProcess.EXPIRED.getProcess());
////				buy.setCancelDatetime(dateStr);
////				buy.setModDatetime(dateStr);
////				buy = buyRepository.saveAndFlush(buy);
////			}else{
////
////
////			}
//
//			if(buy.getPg().equals("DAOU")){
////				if (buy.getType() == GoodsType.MENU_GOODS.getType())
////					buy.setOrderProcess(OrderProcess.CANCEL.getProcess());
//				buy.setProcess(BuyProcess.EXPIRED.getProcess());
////				buy.setCancelDatetime(dateStr);
//				buy.setModDatetime(dateStr);
//				buy = buyRepository.saveAndFlush(buy);
//			}else{
//				if(buy.getPg().equals("LPNG") || buy.getPg().equals("NFC")){
//					if (buy.getType() == GoodsType.MENU_GOODS.getType())
//						buy.setOrderProcess(OrderProcess.REFUND.getProcess());
//					buy.setProcess(BuyProcess.BIZ_CANCEL.getProcess());
//					buy.setCancelDatetime(dateStr);
//					buy.setModDatetime(dateStr);
//					buy = buyRepository.saveAndFlush(buy);
//					return;
//				}
//
//				List<BuyCallback> list = buyCallbackRepository.findAllByBuySeqNo(seqNo);
//				if (list == null) {
//					logger.error("QueueService.bootPayCancel() : 결제 확인정보(BuyCallback)를 DB 에서 찾을 수 없습니다 .");
//				}
//				BuyCallback buyCallback = list.get(0);
//				Boolean success = false;
//				if (status == 0) {
//					success = RootController.bootPayCancel(buyCallback, "사용처리 기간이 지나서 자동 주문 취소");
//				} else { // 1
//					success = RootController.bootPayCancel(buyCallback, "상점주가 주문 미확인으로 자동 주문 취소");
//				}
//
//				if (success || !storeType.equals("PROD")) {
//					if (buy.getType().intValue() == GoodsType.MENU_GOODS.getType())
//						buy.setOrderProcess(OrderProcess.REFUND.getProcess());
//					buy.setProcess(BuyProcess.BIZ_CANCEL.getProcess());
//					buy.setCancelDatetime(dateStr);
//					buy.setModDatetime(dateStr);
//					buy = buyRepository.saveAndFlush(buy);
//				} else {
//
//					buy.setModDatetime(dateStr);
//					buy.setProcess(BuyProcess.ERROR.getProcess()); //서버 에러...
//					buy = buyRepository.saveAndFlush(buy);
//					logger.error("QueueService.bootPayCancel() : 결제 취소 중 오류가 발생하였습니다.");
//				}
//			}
//
//
//
//		} catch(Exception e) {
//			logger.error(AppUtil.excetionToString(e)) ;
//		}
//	}


//	public Integer insertPlusFan(User user, Plus plus) throws ResultCodeException {
//		Integer result = plusSvc.insert(user, plus);
//		if (Const.E_SUCCESS.equals(result)) {
//			Page page = pageSvc.getPageWithUser(plus);
//
//			MsgOnly msg = new MsgOnly();
//			msg.setInput("system");
//			msg.setStatus("ready");
//			msg.setType("push");
//			msg.setMoveType1("inner");
//			msg.setMoveType2("plus");
//			msg.setMoveTarget(user);
//			msg.setSubject(user.getDisplayName() + "님이 회원님의 샵을 플러스 하셨습니다.");
//			msg.setContents(msg.getSubject());
//			msg.setPushCase(Const.BIZ_PUSH_PLUSME);
//			msg.setAppType(Const.APP_TYPE_BIZ);
//			insertMsgBox(user, msg, page.getUser(), Const.APP_TYPE_BIZ);
//		}
//		return result;
//	}

}
