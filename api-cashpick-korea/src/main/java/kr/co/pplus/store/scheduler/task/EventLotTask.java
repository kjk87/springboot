package kr.co.pplus.store.scheduler.task;

import kr.co.pplus.store.mvc.service.EventService;
import kr.co.pplus.store.mvc.service.QueueService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Set;

@DisallowConcurrentExecution
public class EventLotTask extends AbstractTask {
	private final static Logger logger = LoggerFactory.getLogger(EventLotTask.class);

	@Autowired
	EventService svc;
	
	@Autowired
	QueueService queueSvc;

	@Value("${STORE.SCHEDULER_ACTIVATE}")
	Boolean SCHEDULER_ACTIVATE;

	@Override
	public void execute(Map<String, Object> paramMap) throws JobExecutionException {
		if (SCHEDULER_ACTIVATE == null || SCHEDULER_ACTIVATE == false) {
			return;
		}
		logger.info("EventLotTask execute...");
		lot();
//		expiredBuyGoodsByOrderProcess() ;
	}

	@Override
	public void execute(ApplicationContext appContext, Map<String, Object> paramMap) throws JobExecutionException {
		execute(paramMap);
	}

	private void lot() {
		try {
			svc.lotExpiredEventAll();
		}catch (Exception e){
			logger.error("lotExpiredEventAll " + e.toString());
		}

		try {
			svc.concludeLottoAll();
		}catch (Exception e){
			logger.error("concludeLottoAll " + e.toString());
		}


	}

}
