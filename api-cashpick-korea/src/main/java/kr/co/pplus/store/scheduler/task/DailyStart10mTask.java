package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.api.jpa.service.MemberService;
import kr.co.pplus.store.api.jpa.service.PurchaseService;
import kr.co.pplus.store.api.util.AppUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@DisallowConcurrentExecution
public class DailyStart10mTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(DailyStart10mTask.class);


	@Autowired
	MemberService memberService;

	@Autowired
	PurchaseService purchaseService;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;
	
	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}

		logger.info("DailyStart10mTask execute...");

		expiredTicketProductGivePoint();
		restAdCount();
		
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}


	private void restAdCount(){
		try{
			memberService.adRewardReset();
		}catch (Exception e){
			logger.error(AppUtil.excetionToString(e));
		}
	}

	private void expiredTicketProductGivePoint(){
		try{
			purchaseService.expiredTicketProductGivePoint();
		}catch (Exception e){
			logger.error(AppUtil.excetionToString(e));
		}
	}

}
