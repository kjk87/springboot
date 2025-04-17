package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.api.jpa.service.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.PageService;
import kr.co.pplus.store.mvc.service.QueueService;
import kr.co.pplus.store.mvc.service.UserService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class DailyStartCommonTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(DailyStartCommonTask.class);
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	PageService pageSvc;
	
//	@Autowired
//	MobileGiftService mobileSvc;

	@Autowired
	GiftishowService giftishowService;

	@Autowired
	QueueService queueSvc;

//	@Autowired
//	BuyGoodsService buyGoodsService;

	@Autowired
	PurchaseService purchaseService;

	@Autowired
	EventService eventService;

	@Autowired
	ShippingService shippingService;

	@Autowired
	MemberService memberService;

	@Autowired
	LuckyBoxService luckyBoxService;

	@Autowired
	LuckyPickService luckyPickService;

	@Autowired
	LuckyCouponService luckyCouponService;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;
	
	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("DailyStartCommonTask execute...");

		//탈퇴 신청한지 15일이 지난 회원들을 탈퇴 처리 한다.
		leaveUserAll();
		//모바일상품권 연동처리
		syncMobileGift();
		//7일지난구매건 완료처리
		completeBuy();
		//경품만료처리
		expiredEventWin();

		deliveryComplete();

		couponExpired();

	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	/**
	 * 쿠폰 템플릿 및 쿠폰 만료 처리를 한다.
	 */
	private void expiredEventWin() {
		try {
			eventService.expiredEventWin();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void leaveUserAll() {
		try{
			userSvc.leaveAll();
		}catch (Exception e){
			logger.error(AppUtil.excetionToString(e));
		}
	}
	
	private void syncMobileGift() {
		try {
			giftishowService.syncGoodsList();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void completeBuy(){
		try{
			purchaseService.setCompletePurchaseProductList();
		}catch (Exception e){
			logger.error(AppUtil.excetionToString(e));
		}
	}

	private void deliveryComplete(){
		try{
			shippingService.deliveryPurchaseComplete();
		}catch (Exception e){
			logger.error(e.toString());
		}

		try{
			luckyBoxService.deliveryComplete();
		}catch (Exception e){
			logger.error(e.toString());
		}

		try{
			luckyPickService.deliveryComplete();
		}catch (Exception e){
			logger.error(e.toString());
		}

	}

	public void couponExpired(){
		try{
			luckyCouponService.expireCoupon();
		}catch (Exception e){
			logger.error(e.toString());
		}

	}


}
